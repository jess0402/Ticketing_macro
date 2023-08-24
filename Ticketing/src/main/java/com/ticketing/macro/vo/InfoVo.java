package com.ticketing.macro.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class InfoVo {

    private String id;              // id
    private String password;        // pw
    private String birth;           // 생일 YYMMDD
    private String musicalCode;     // 작품 코드
    private String nop;             // 인원수(Number Of People)
    private String date;            // 날짜 YYYY-MM-DD
    private String round;           // 회차 (1회차인지 2회차인지)
    private int startRow;           // 시작열
    private int endRow;             // 종료열
    private int startSeat;          // 시작 좌석
    private int endSeat;            // 종료 좌석

}
