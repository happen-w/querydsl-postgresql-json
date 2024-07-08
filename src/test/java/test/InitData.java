package test;

import entity.Book;
import entity.Library;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Author: Happen
 * Date: 2024/7/8 11:28
 **/
public class InitData {


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


        Library library = new Library();
        library.setName("天河图书馆");
        entityManager.persist(library);

        Library library2 = new Library();
        library2.setName("海珠图书馆");
        entityManager.persist(library2);


        Book book = new Book();
        book.setAuthor("吴承恩");
        book.setTitle("西游记");
        book.setLibrary(library);
        entityManager.persist(book);

        Book book1 = new Book();
        book1.setAuthor("曹雪芹");
        book1.setTitle("红楼梦");
        book1.setLibrary(library);
        entityManager.persist(book1);


        Book book2 = new Book();
        book2.setAuthor("罗贯中");
        book2.setTitle("三国演义");
        book2.setLibrary(library2);
        entityManager.persist(book2);

        Book book3 = new Book();
        book3.setAuthor("施耐庵");
        book3.setTitle("水浒传");
        book3.setLibrary(library2);
        entityManager.persist(book3);


        transaction.commit();
        entityManager.close();
        sessionFactory.close();
    }




}
