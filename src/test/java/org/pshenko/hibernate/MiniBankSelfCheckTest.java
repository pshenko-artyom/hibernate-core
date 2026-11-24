package org.pshenko.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.pshenko.hibernate.account.Account;
import org.pshenko.hibernate.account.AccountService;
import org.pshenko.hibernate.account.CloseResult;
import org.pshenko.hibernate.account.TransferResult;
import org.pshenko.hibernate.config.ApplicationConfiguration;
import org.pshenko.hibernate.exception.BankException;
import org.pshenko.hibernate.user.User;
import org.pshenko.hibernate.user.UserService;
import org.pshenko.hibernate.util.TransactionHelper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("MiniBank-тесты по ТЗ")
class MiniBankSelfCheckTest {

    private static AnnotationConfigApplicationContext ctx;
    private static UserService userService;
    private static AccountService accountService;
    private static TransactionHelper txHelper;
    private static SessionFactory sf;

    @BeforeAll
    static void setUpContext() {
        ctx = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        userService = ctx.getBean(UserService.class);
        accountService = ctx.getBean(AccountService.class);
        txHelper = ctx.getBean(TransactionHelper.class);
        sf = ctx.getBean(SessionFactory.class);
        System.out.println("\n[SETUP] Spring context + SessionFactory созданы");
    }

    @AfterAll
    static void closeContext() {
        if (ctx != null) ctx.close();
        System.out.println("[TEARDOWN] Контекст закрыт\n");
    }

    @BeforeEach
    void cleanDb() {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            s.createMutationQuery("DELETE FROM Account").executeUpdate();
            s.createMutationQuery("DELETE FROM User").executeUpdate();
            tx.commit();
        }
    }

    private Account readAccount(int id) {
        try (Session s = sf.openSession()) {
            return s.find(Account.class, id);
        }
    }

    private User readUser(int id) {
        try (Session s = sf.openSession()) {
            return s.createQuery("""
                    select u from User u left join fetch u.accounts where u.id = :id
                    """, User.class)
                .setParameter("id", id)
                .uniqueResult();
        }
    }

    private long countUsers() {
        try (Session s = sf.openSession()) {
            return s.createQuery("select count(u) from User u", Long.class).getSingleResult();
        }
    }

    @Test
    @Order(1)
    @DisplayName("1. Создание двух юзеров, у каждого первый счёт 500. Повторный/пустой логин — ошибка")
    void scenario1_createUsers() {
        System.out.println("\n== Сценарий 1: создание двух юзеров ==");

        User u1 = userService.create("Artyom");
        User u2 = userService.create("Vasya");
        System.out.println("user1: id=" + u1.getId() + ", login=" + u1.getLogin());
        System.out.println("user2: id=" + u2.getId() + ", login=" + u2.getLogin());

        List<Account> accs1 = readUser(u1.getId()).getAccounts();
        List<Account> accs2 = readUser(u2.getId()).getAccounts();
        assertThat(accs1).hasSize(1);
        assertThat(accs1.get(0).getMoneyAmount()).isEqualTo(500);
        assertThat(accs2).hasSize(1);
        assertThat(accs2.get(0).getMoneyAmount()).isEqualTo(500);
        System.out.println("[OK] у каждого по одному счёту с балансом 500");

        assertThatThrownBy(() -> userService.create("Artyom"))
            .isInstanceOf(BankException.class)
            .hasMessageContaining("already exists");
        System.out.println("[OK] повторный логин -> BankException");

        assertThatThrownBy(() -> userService.create(""))
            .isInstanceOf(BankException.class)
            .hasMessageContaining("cannot be empty");
        System.out.println("[OK] пустой логин -> BankException");
    }

    @Test
    @Order(2)
    @DisplayName("2. Перевод 100 между своими счетами: 400/600, без комиссии")
    void scenario2_transferSameUser() {
        System.out.println("\n== Сценарий 2: перевод между своими счетами ==");

        User u = userService.create("Artyom");
        Account a1 = readUser(u.getId()).getAccounts().get(0);
        Account a2 = accountService.create(u.getId());
        System.out.println("a1.id=" + a1.getId() + ", a2.id=" + a2.getId());

        TransferResult r = accountService.transfer(a1.getId(), a2.getId(), 100);
        System.out.println("transfer: " + r);
        assertThat(r.isSameUser()).isTrue();
        assertThat(r.commission()).isZero();
        assertThat(r.amountAfterCommission()).isEqualTo(100);

        assertThat(readAccount(a1.getId()).getMoneyAmount()).isEqualTo(400);
        assertThat(readAccount(a2.getId()).getMoneyAmount()).isEqualTo(600);
        System.out.println("[OK] 400 / 600, комиссии нет");
    }

    @Test
    @Order(3)
    @DisplayName("3. Перевод 100 между разными юзерами: 400/598, комиссия 2. Границы 1 и 25")
    void scenario3_transferDifferentUsers() {
        System.out.println("\n== Сценарий 3: перевод между разными юзерами ==");

        User u1 = userService.create("Artyom");
        User u2 = userService.create("Vasya");
        Account a1 = readUser(u1.getId()).getAccounts().get(0);
        Account a2 = readUser(u2.getId()).getAccounts().get(0);

        TransferResult r = accountService.transfer(a1.getId(), a2.getId(), 100);
        System.out.println("transfer(100): commission=" + r.commission()
            + ", received=" + r.amountAfterCommission());
        assertThat(r.isSameUser()).isFalse();
        assertThat(r.commission()).isEqualTo(2);
        assertThat(r.amountAfterCommission()).isEqualTo(98);
        assertThat(readAccount(a1.getId()).getMoneyAmount()).isEqualTo(400);
        assertThat(readAccount(a2.getId()).getMoneyAmount()).isEqualTo(598);
        System.out.println("[OK] 400 / 598, комиссия 2");

        // Границы округления из ТЗ: 1 -> 1, 25 -> 25
        TransferResult r1 = accountService.transfer(a1.getId(), a2.getId(), 1);
        assertThat(r1.commission()).isZero();
        assertThat(r1.amountAfterCommission()).isEqualTo(1);
        System.out.println("[OK] 1 -> 1 (комиссия 0)");

        TransferResult r25 = accountService.transfer(a1.getId(), a2.getId(), 25);
        assertThat(r25.commission()).isZero();
        assertThat(r25.amountAfterCommission()).isEqualTo(25);
        System.out.println("[OK] 25 -> 25 (комиссия 0)");
    }

    @Test
    @Order(4)
    @DisplayName("4. Ошибки: отрицательные/нулевые суммы, неизвестные ID, недостаток, тот же счёт")
    void scenario4_errors() {
        System.out.println("\n== Сценарий 4: ошибки ==");

        User u1 = userService.create("Artyom");
        User u2 = userService.create("Vasya");
        Account a1 = readUser(u1.getId()).getAccounts().get(0);
        Account a2 = readUser(u2.getId()).getAccounts().get(0);

        assertThatThrownBy(() -> accountService.deposit(a1.getId(), -100))
            .isInstanceOf(BankException.class);
        System.out.println("[OK] deposit(-100) -> BankException");

        assertThatThrownBy(() -> accountService.deposit(a1.getId(), 0))
            .isInstanceOf(BankException.class);
        System.out.println("[OK] deposit(0) -> BankException");

        assertThatThrownBy(() -> accountService.withdraw(a1.getId(), 100_000))
            .isInstanceOf(BankException.class)
            .hasMessageContaining("insufficient funds");
        System.out.println("[OK] withdraw(overdraft) -> insufficient funds");

        assertThatThrownBy(() -> accountService.transfer(a1.getId(), a1.getId(), 10))
            .isInstanceOf(BankException.class)
            .hasMessageContaining("same account");
        System.out.println("[OK] transfer(same) -> BankException");

        assertThatThrownBy(() -> accountService.deposit(999, 10))
            .isInstanceOf(BankException.class)
            .hasMessageContaining("not found");
        System.out.println("[OK] deposit(unknownId) -> not found");

        assertThat(readAccount(a1.getId()).getMoneyAmount()).isEqualTo(500);
        assertThat(readAccount(a2.getId()).getMoneyAmount()).isEqualTo(500);
        System.out.println("[OK] балансы после ошибок не изменились");
    }

    @Test
    @Order(5)
    @DisplayName("5. Закрытие непустого счёта: остаток на min ID. Единственный нельзя")
    void scenario5_close() {
        System.out.println("\n== Сценарий 5: закрытие счёта ==");

        User u = userService.create("Artyom");
        Account a1 = readUser(u.getId()).getAccounts().get(0);
        Account a2 = accountService.create(u.getId());
        accountService.deposit(a2.getId(), 200);
        System.out.println("a1.id=" + a1.getId() + " (500), a2.id=" + a2.getId() + " (700 после deposit)");

        CloseResult r = accountService.close(a2.getId());
        System.out.println("closed=" + a2.getId() + ", target=" + r.targetAccountId()
            + ", transferred=" + r.transferredAmount());

        assertThat(r.targetAccountId()).isEqualTo(a1.getId());
        assertThat(r.transferredAmount()).isEqualTo(700);
        assertThat(readAccount(a1.getId()).getMoneyAmount()).isEqualTo(1200);
        assertThat(readAccount(a2.getId())).isNull();
        System.out.println("[OK] остаток 700 ушёл на a1 (итого 1200), a2 удалён");

        assertThatThrownBy(() -> accountService.close(a1.getId()))
            .isInstanceOf(BankException.class)
            .hasMessageContaining("only account");
        System.out.println("[OK] закрытие единственного -> BankException");
    }

    @Test
    @Order(6)
    @DisplayName("6. Перезапуск: данные читаются из БД новой сессией")
    void scenario6_persistence() {
        System.out.println("\n== Сценарий 6: сохранение в БД ==");

        User u = userService.create("Persisted");
        Integer userId = u.getId();
        Integer accId = readUser(userId).getAccounts().get(0).getId();
        System.out.println("создан user id=" + userId + ", account id=" + accId);

        User fromDb = readUser(userId);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getLogin()).isEqualTo("Persisted");
        assertThat(fromDb.getAccounts()).hasSize(1);
        assertThat(fromDb.getAccounts().get(0).getId()).isEqualTo(accId);
        System.out.println("[OK] user и account читаются из БД новой сессией");
    }

    @Test
    @Order(7)
    @DisplayName("7. Rollback при ошибке в transfer после изменения балансов")
    void scenario7_rollbackTransfer() {
        System.out.println("\n== Сценарий 7: rollback в transfer ==");

        User u1 = userService.create("Artyom");
        User u2 = userService.create("Vasya");
        Account a1 = readUser(u1.getId()).getAccounts().get(0);
        Account a2 = readUser(u2.getId()).getAccounts().get(0);
        int before1 = a1.getMoneyAmount();
        int before2 = a2.getMoneyAmount();
        System.out.println("до: a1=" + before1 + ", a2=" + before2);

        assertThatThrownBy(() ->
            txHelper.executeInTransaction(session -> {
                Account src = session.find(Account.class, a1.getId());
                Account dst = session.find(Account.class, a2.getId());
                src.setMoneyAmount(src.getMoneyAmount() - 100);
                dst.setMoneyAmount(dst.getMoneyAmount() + 98);
                throw new RuntimeException("artificial failure");
            })
        ).isInstanceOf(RuntimeException.class);
        System.out.println("искусственная ошибка брошена внутри транзакции");

        int after1 = readAccount(a1.getId()).getMoneyAmount();
        int after2 = readAccount(a2.getId()).getMoneyAmount();
        assertThat(after1).isEqualTo(before1);
        assertThat(after2).isEqualTo(before2);
        System.out.println("[OK] после ошибки a1=" + after1 + ", a2=" + after2 + " (откат сработал)");
    }

    @Test
    @Order(8)
    @DisplayName("8. Rollback при ошибке в create (user persist, потом fail)")
    void scenario8_rollbackCreate() {
        System.out.println("\n== Сценарий 8: rollback в create ==");

        long usersBefore = countUsers();
        System.out.println("юзеров до: " + usersBefore);

        assertThatThrownBy(() ->
            txHelper.executeInTransaction(session -> {
                User user = new User("ShouldNotPersist");
                session.persist(user);
                throw new BankException("artificial failure");
            })
        ).isInstanceOf(BankException.class);
        System.out.println("искусственная ошибка после persist(user)");

        long usersAfter = countUsers();
        assertThat(usersAfter).isEqualTo(usersBefore);
        System.out.println("[OK] юзеров после: " + usersAfter + " (откат сработал)");
    }
}