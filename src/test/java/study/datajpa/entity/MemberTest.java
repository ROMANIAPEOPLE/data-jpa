package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@SpringBootTest
@Transactional
class MemberTest {
    @PersistenceContext
    EntityManager em;

    @Test
    public void testEntity(){
        Team teamA=new Team("TeamA");
        Team teamB=new Team("TeamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member=new Member("member1", 10, teamA);
        Member member2=new Member("member2", 11, teamA);
        Member member3=new Member("member3", 12, teamA);
        Member member4=new Member("member4", 13, teamA);
        em.persist(member);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        em.flush();
        em.clear();

        List<Member> list = em.createQuery(" select m from Member m",Member.class).getResultList();

        for (Member member1 : list) {
            System.out.println(member1);
            System.out.println(member1.getTeam());
            
        }


    }

}