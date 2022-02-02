# Querydsl

1. querydsl 라이브러리 추가

build.gradle
```groovy
buildscript {
    ext {
        queryDslVersion = "5.0.0"
    }
}

plugins {    
    //querydsl 추가
    id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"
}
dependencies {
   
    // QueryDSL
    implementation "com.querydsl:querydsl-jpa:${queryDslVersion}"
    annotationProcessor "com.querydsl:querydsl-apt:${queryDslVersion}"
    
}
def querydslDir = "$buildDir/generated/querydsl"

querydsl {
    jpa = true
    querydslSourcesDir = querydslDir
}

sourceSets {
    main.java.srcDir querydslDir
}

configurations {
    querydsl.extendsFrom compileClasspath
}

compileQuerydsl {
    options.annotationProcessorPath = configurations.querydsl
}
```

2. Q클래스 생성
   Gradle Project - Tasks - other - compileJava 실행
   src/main/generated에 Q클래스 생성 확인
   
3. EntityManager, JPAQueryFactory
```java
    
EntityManager em; // 주입
JPAQueryFactory queryFactory; // 주입
queryFactory = new JPAQueryFactory(em);
    
```

4. 쿼리 작성 예시
```java
queryFactory
        .select(member)
        .from(member)
        .where()
        .fetch()
```


### Spring Data JPA 적용하기

1. Repository 생성 
   - JpaRepository, MemberRepositoryCustom 상속
   - MemberRepositoryCustom은 querydsl 쿼리 작성을 위한 커스텀 파일
```java
public interface MemberRepository extends JpaRepository<Member,Long>,MemberRepositoryCustom {

    List<Member> findByUsername(String username);


}
```

2. 커스텀 파일 작성 (MemberRepositoryCustom.java, MemberRepositoryImpl.java)
   
MemberRepositoryCustom.java
```java

public interface MemberRepositoryCustom {
   List<MemberTeamDto> search(MemberSearchCondition condition);
  }

```
MemberRepositoryImpl.java
```java

// ...

 @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .fetch();
    }
    
```

### 기본 문법

