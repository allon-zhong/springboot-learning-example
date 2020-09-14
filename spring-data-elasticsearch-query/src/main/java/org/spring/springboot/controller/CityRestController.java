package org.spring.springboot.controller;

import org.spring.springboot.domain.City;
import org.spring.springboot.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 城市 Controller 实现 Restful HTTP 服务
 * <p>
 * Created by bysocket on 20/06/2017.
 */
@RestController
public class CityRestController {

    @Autowired
    private CityService cityESServiceImpl;
    @Autowired
    private CityService restClientServiceImpl;

    /**
     * 插入 ES 新城市
     *
     * @param city
     * @return
     */
    @RequestMapping(value = "/api/city", method = RequestMethod.POST)
    public Long createCity(@RequestBody City city) {
        return cityESServiceImpl.saveCity(city);
    }

    @RequestMapping(value = "/api/rest/city", method = RequestMethod.POST)
    public Long createCityRest(@RequestBody City city) {
        return restClientServiceImpl.saveCity(city);
    }

    /**
     * 搜索返回分页结果
     *
     * @param searchContent 搜索内容
     * @return
     */
    @RequestMapping(value = "/api/city/search", method = RequestMethod.GET)
    public List<City> searchCity(@RequestParam(value = "searchContent") String searchContent) {
        return restClientServiceImpl.searchCity(searchContent);
    }
}
