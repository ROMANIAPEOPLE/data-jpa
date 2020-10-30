# data-jpa
SPRING DATA JPA STUDY

## 스프링 데이터 JPA 공부한 내용 정리 / 코드 저장소

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

