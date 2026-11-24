package org.pshenko.hibernate.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class TransactionHelper {
    private final SessionFactory sessionFactory;

    public TransactionHelper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T executeInTransaction(Function<Session, T> action) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();
        boolean owner = transaction.getStatus() == TransactionStatus.NOT_ACTIVE;
        if (owner) {
            transaction.begin();
        }
        try {
            T result = action.apply(session);
            if (owner) {
                transaction.commit();
            }
            return result;
        } catch (RuntimeException e) {
            if (owner && transaction.getStatus() == TransactionStatus.ACTIVE) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (owner && transaction.getStatus() == TransactionStatus.ACTIVE) {
                session.close();
            }
        }
    }
}
