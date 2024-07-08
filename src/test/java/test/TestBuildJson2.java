package test;

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
public class TestBuildJson2 {


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



        JPAQuery<Library> query = new JPAQuery<>(entityManager)
                .select(QLibrary.library)
                .from(QLibrary.library)
                .where(QLibrary.library.id.eq(1L));
                ;
        List<Library> fetch = query.fetch();
        for (Library library : fetch) {
            System.out.println("=============");
            System.out.println(library);
        }
        transaction.commit();
        entityManager.close();
        sessionFactory.close();
    }
}
