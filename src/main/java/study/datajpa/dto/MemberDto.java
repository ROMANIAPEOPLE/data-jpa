package study.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MemberDto {
    private Long id;
    private String username;
    private  String teamName;

    public MemberDto(Long id, String username){
        this.id = id;
        this.username=username;

    }


}
