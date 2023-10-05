package com.ticketing.macro.service;

import com.ticketing.macro.vo.InfoVo;
import org.springframework.stereotype.Service;

@Service
public interface TicketingService {

    public String interparkTicketing(InfoVo infoVo);
}
