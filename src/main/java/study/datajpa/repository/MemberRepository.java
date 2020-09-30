package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    List<Member> findTop3HelloBy();

    @Query("select m from Member m where m.username= :username and m.age= :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    //Dto를 조회할때는 조금 복잡한 코드가 쓰인다.

    @Query("select new study.datajpa.dto.MemberDto(m.id,m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    //다양한 반환타입을 가지는 스프링 데이터 JPA
    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);

    Page<Member> findByAge(int age, Pageable pageable);


    //벌크성 수정쿼리(조건에 맞는거 전체 수정)
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age+1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);


    //N+1 문제를 해결하기 위한 fetch join
    @Query("select  m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    //fetch조인을 @EntityGraph로 대체할 수 있음.
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //jpql도 @EntityGraph 사용 가능
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();


    @EntityGraph(attributePaths = {"team"})
    List<Member> findByUsername(@Param("username") String username);


    //별로 쓸 일 없음
    @QueryHints(value =@QueryHint(name ="org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);


}
