package com.ticketing.macro.controller;

import com.ticketing.macro.vo.InfoVo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TicketingController {

    @PostMapping("/interparkTicketing")
    @ResponseBody
    public ResponseEntity<String> interparkTicktingConn(@Validated @ModelAttribute InfoVo infoVo) {
        try{
            System.out.println("id - " + infoVo.getId());
            return ResponseEntity.ok("{\"message\": \"성공\"}");
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"에러 발생: " + e.getMessage() + "\"}");
        }
    }
}
