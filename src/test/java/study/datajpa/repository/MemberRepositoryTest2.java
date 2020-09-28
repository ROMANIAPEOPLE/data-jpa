package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest2 {
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).get();

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());

    }

}