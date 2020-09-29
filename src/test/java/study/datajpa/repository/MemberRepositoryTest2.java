package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;

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

    @Test
    public void findUsernameList(){
    Member m1=new Member("AAA",10);
    Member m2=new Member("BBB",20);
    memberRepository.save(m1);
    memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s=" + s);
        }

    }


      //페이징 테스트
    @Test
    public void paging() throws Exception {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age =10;

        PageRequest pageRequest=PageRequest.of(0,3,Sort.by(Sort.Direction.DESC, "username"));

        //when
         Page<Member>  page=memberRepository.findByAge(age,pageRequest);

         //페이지 계산 공식 적용...
        // totalPage = totalCount / size ...
        // 마지막 페이지 ...
        // 최초 페이지 ..x

        //API로 반환할때는, Entity를 노출시키면 안되기때문에 Dto로 변환해서 사용해야한다.
        //이 경우 Page의 map을 이용하자.
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(),member.getUsername(),null));




    }

}