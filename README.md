# data-jpa
SPRING DATA JPA STUDY

## 스프링 데이터 JPA 공부한 내용 정리 

<details>
  <summary>1. 페이징 방법</summary>
  <div markdown="1">
  
  ## 페이징 방법

#### 1. 순수 JPA 페이징과 정렬

- 검색 조건 : 나이 10살

- 정렬 조건  : 이름으로 내림차순

- 페이징 조건 : 첫 번째 페이지, 페이지당 보여줄 데이터는 3건

- ```java
  public List<Member> findByPage(int age, int offset, int limit) {
      em.createQuery("select m from Member m where m.age = :age order by m.username desc")
          .setParameter("age",age)
          .setFirstResult(offset)
          .setMaxResults(limit)
          .getResultList();
  }
  ```

- 보통은 페이징을 할때 TotalCount (총 몇 페이지 인가?) 를 함께 가져와야 한다.

  - ```java
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
            .setParameter("age", age)
            .getSingleResult();
    }
    ```

  - 토탈에는 sort가 필요가 없다. 



#### 2. SPRING DATA JPA 페이징

- ```java
  org.springframework.data.domain.Sort : 정렬기능
  ```

- ```java
  org.springframework.data.domain.Pageable : 페이징 기능
  ```

- 페이징을 제공하는 Page<Entity명>을 사용한다.

- ```java
  Page<Member> findByAge(int age, Pageable pageable);
  ```

- pageable에는 pageRequest를 구현해서 파라미터로 넘겨주면 된다.

- ```java
  PageReuqst pageRequest = PageRequest.of(0,3, Sort.by(Sort.Direction.DESC,"username"));
  //0은 시작 데이터 , 3은 size
  
  Page<Member> page = memberRepository.findByAge(age,pageRequest);
  ```

- List<Member> contents = page.getContent(); 로 0부터 총 3개의 데이터를 contetns안에 넣어서 리턴한다.

- totalCount 를 따로 구할 필요가 없다. 반환타입이 page면  totalCount를 반환해주는 메서드를 제공해준다.

  - page 쿼리가 실행될 때 자동으로 totalCount를 구하는 쿼리를 날린다.

  - ```java
    long totalCount = page.getTotalElements();
    ```

```
int getNumber();           //현재 페이지

int getSize();              	//페이지 크기

int getTotalPages();         //전체 페이지 수

int getNumberOfElements(); 	//현재 페이지에 나올 데이터 수

long getTotalElements();     //전체 데이터 수

boolean hasPreviousPage();  	//이전 페이지 여부

boolean isFirstPage();       	//현재 페이지가 첫 페이지 인지 여부

boolean hasNextPage();      //다음 페이지 여부

boolean isLastPage();        //현재 페이지가 마지막 페이지 인지 여부

Pageable nextPageable();     //다음 페이지 객체, 다음 페이지가 없으면 null

Pageable previousPageable();  //다음 페이지 객체, 이전 페이지가 없으면 null

List<T> getContent();        //조회된 데이터

boolean hasContent();       	//조회된 데이터 존재 여부

Sort getSort();              //정렬정보
```



- Sliece : 예를들어 limit을 0번째부터 3개를 가져오라는 설정을 하면, 3개가 아닌 3+1개로 총 4개를 가져오게 된다.

  - 즉, limit+1개를 가져온다.

  - ```java
    Sliece<Member> page = memberRepository.findByAge(age,pageRequest);
    ```

  - 모바일에서 [더보기] 라는 것으로 많이 사용된다.

  - Slice는 totalCout 쿼리가 자동으로 날라가지 않는다.

    

- List로 바로 받아오는것도 가능하다.

  - ```java
    List<Member> page = memberRepository.findByAge(age, pageRequest);
    ```

- 마찬가지로 Page로 조회해온 데이터들도 Entity를 그대로 노출시키면 안된다. DTO로 변환해서 반환해줘야 한다.

  - ```java
    Page<Member> page = memberRepository.findByAge(age, pageRequest);
    page<MemberDto> toMap = page.map(m->new MemberDto(m.getId(),m.getUsername()));
    ```

    
  </div>
</details>

<details>
  <summary>2. 벌크성 수정쿼리 + @EntityGraph</summary>
  <div markdown="1">
  
  # 벌크성 수정 쿼리

- 무언가를 수정하는 쿼리를 날릴때는 @Modifying을 적어줘야 한다.

- 벌크성 수정쿼리의 예시로, 회원의 나이를 1씩 증가시키는 쿼리를 살펴보자

  - ```java
    @Query("update Member m set m.age = m.age +1 where m.age >=:age")
    int bulkAgePlus(@Param"age" int age);
    ```

- 벌크 연산 이후에는 꼭 영속성 컨텍스트를 clear 해줘야 한다.

  - DB에는 벌크성 수정쿼리를 실행한 값이 반영되어있는데 영속성 컨텍스트에는 반영되기 전 값으로 남아있다.

  - 벌크 연산 이후에 다른 로직이 실행되거나 이어지는게 없으면 상관 없지만, 다른 작업이 추가로 이어진다면 반드시 영속성 컨텐스트를 clear 해야 정상적으로 반영된다.

  - **이러한 문제를 해결하기 위해 스프링 데이터JPA는 기능을 지원한다**

    - @Modifying(clearAutotmatically = true) 로 설정하면 자동으로 영속성 컨텍스트를 clear해준다.

    



## @EntityGraph란?

```java
@Test
public void findMemberLazy() {
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    Member member1 = new Member("member1,10,teamA");
    Member member2 = new Member("member2,12,teamB");
    memberRepository.save(member1);
    memberRepository.save(member2);
    
    em.flush();
    em.clear();
    
    List<Member> members = memberRepository.findAll();
    
    
}
```

- EntityGraph를 이해하기 위해서는 Fetch Join과 지연 로딩 이라는 개념을 알아야한다.

- 간단하게 설명해서, 실무에서 연관관계 매핑시 모두 LAZY 로딩으로 설정해야하는데

  - 지연로딩(LAZY)으로 설정시, 연관된 엔티티를 조회하는 동시에 한번에 쿼리문을 날리지 않고, 그 엔티티가 사용되는 시점에 추가로 쿼리를 날리는 방법이다.

  - N+1 문제가 발생하는데, 만약 조회된 Member가 위 예제처럼 2개라고 생각해보자.

    - 그러면 Member를 조회하는 쿼리를 날린 후,  Team을 조회하는 쿼리가 2개 더 날라간다. 

    - 이러한 문제를 해결하기 위해 Fetch Join 이라는 것을 사용해야 한다.

    - ```java
      @Query("select m from Member m left join fetch m.team")
      List<Member> findMemberFechJoin();
      ```

- @EntityGraph 사용하기

  - ```java
    @OVerride
    @EntityGraph(attributePaths ={"team"})
    List<Member> findAll();
    //Fetch join 대신 EntityGraph 어노테이션을 추가하고, fetch join 할 엔티티 명을 넣어주면 된다.
    ```

  - ```java
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();
    //JPQL을 사용하면서 , Fetch join만 생략 후 EntityGraph 어노테이션 사용 가능
    ```

  - ```java
    @EntityGraph(attributePaths= {"team"})
    List<Member> findEntityByUsername(@Param("username") String username);
    //메서드 이름으로 쿼리를 사용할때도 EntityGraph를 사용할 수 있다.
    ```

    
  </div>
</details>



