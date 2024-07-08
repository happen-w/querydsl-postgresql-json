package test;

import com.github.happen.dialect.CustomExpressions;
import com.querydsl.core.Tuple;
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

import java.util.List;

import static com.github.happen.dialect.CustomExpressions.jsonBuildTemplate;
import static com.github.happen.dialect.CustomExpressions.jsonbAggTemplate;

/**
 * Author: Happen
 * Date: 2024/7/8 11:28
 **/
public class TestBuildJson {


    public static void main(String[] args) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.driver_class", "org.postgresql.Driver")
                .applySetting("hibernate.connection.url", "jdbc:postgresql://localhost:5432/library")
                .applySetting("hibernate.connection.username", "postgres")
                .applySetting("hibernate.connection.password", "postgres")
                .applySetting("hibernate.dialect", "com.github.happen.dialect.CustomPostgreSQLDialect")
                .applySetting("hibernate.show_sql", true)
                .build();
        SessionFactory sessionFactory = new MetadataSources(registry)
                .addAnnotatedClass(Book.class)
                .addAnnotatedClass(Library.class)
                .buildMetadata()
                .buildSessionFactory();

        Session entityManager = sessionFactory.openSession();
        Transaction transaction = entityManager.beginTransaction();


        StringTemplate book = jsonbAggTemplate(jsonBuildTemplate(QBook.book));
        JPAQuery<Tuple> query = new JPAQuery<>(entityManager)
                .select(QLibrary.library, book)
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
            System.out.println(tuple.get(book));
            System.out.println("=============");
        }

        transaction.commit();
        entityManager.close();
        sessionFactory.close();
    }
}
