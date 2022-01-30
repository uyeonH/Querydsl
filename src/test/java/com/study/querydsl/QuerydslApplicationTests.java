package com.study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.querydsl.entity.QUser;
import com.study.querydsl.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Commit
class QuerydslApplicationTests {

    @Autowired
    EntityManager em;

    @Test
    void contextLoads() {
        User user = new User();
        em.persist(user);
        JPAQueryFactory query = new JPAQueryFactory(em);
        QUser qUser = QUser.user;
        User result = query.selectFrom(qUser).fetchOne();
        assertThat(result).isEqualTo(user);
        assertThat(result.getId()).isEqualTo(user.getId());
    }

}
