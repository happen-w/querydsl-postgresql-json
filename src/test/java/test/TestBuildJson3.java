package test;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import entity.Book;
import entity.Library;
import entity.QBook;
import entity.QLibrary;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.PostgreSQLJsonPGObjectJsonbType;

import java.util.List;


public class TestBuildJson3 {

    // 最开始的写法
    public static void main(String[] args) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.driver_class", "org.postgresql.Driver")
                .applySetting("hibernate.connection.url", "jdbc:postgresql://localhost:5432/postgres")
                .applySetting("hibernate.connection.username", "postgres")
                .applySetting("hibernate.connection.password", "postgres")
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.show_sql", true)
                .build();
        SessionFactory sessionFactory = new MetadataSources(registry)
                .addAnnotatedClass(Book.class)
                .addAnnotatedClass(Library.class)
                .buildMetadata()
                .buildSessionFactory();

        Session entityManager = sessionFactory.openSession();
        Transaction transaction = entityManager.beginTransaction();

        String template = String.format("CAST(json_object_agg({0}) as text)");
        StringTemplate tp = Expressions.stringTemplate(template, QBook.book);

        JPAQuery<Tuple> query = new JPAQuery<>(entityManager)
                .select(QLibrary.library, tp)
                .from(QLibrary.library)
                .leftJoin(QBook.book)
                .on(QLibrary.library.eq(QBook.book.library))
                .groupBy(QLibrary.library.id)
                ;

        System.out.println(query);
        List<Tuple> fetch = query.fetch();
        for (Tuple tuple : fetch) {
            System.out.println("=============");
            System.out.println(tuple.get(QLibrary.library));
            System.out.println(tuple.get(tp));
            System.out.println("=============");
        }

        transaction.commit();
        entityManager.close();
        sessionFactory.close();
    }
}
