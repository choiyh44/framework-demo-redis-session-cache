/**
 * 
 */
package kr.co.ensmart.frameworkdemo.app.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import kr.co.ensmart.frameworkdemo.app.dto.User;
import lombok.extern.slf4j.Slf4j;

/**
 * @author choiyh44
 * @version 1.0
 * @since 2022. 2. 18.
 *
 */
@Service
@Slf4j
public class SampleRedisCacheService {

    @Cacheable(value="SampleService.findByUserName", key="#userName", cacheManager="hourlyCacheManager")
    public User findByUserName(String userName) {
        User user = new User();
        user.setId(10);
        user.setUserName(userName);
        user.setPassword("pswd");
        log.info("SampleService.findByUserName user: {}", user);
        return user;
    }
    
    @Cacheable(value="SampleService.findByUserId", key="#userRequest.id", cacheManager="hourlyCacheManager")
    public User findByUserId(User userRequest) {
        User user = new User();
        user.setId(10);
        user.setUserName(userRequest.getId()+"");
        user.setPassword("pswd");
        log.info("SampleService.findByUserId user: {}", user);
        return user;
    }
    
    @CacheEvict(value="SampleService.findByUserId", key="#userRequest.id", cacheManager="hourlyCacheManager")
    public User modifyByUserId(User userRequest) {
        User user = new User();
        user.setId(10);
        user.setUserName(userRequest.getId()+"");
        user.setPassword("pswd");
        log.info("SampleService.modifyByUserId user: {}", user);
        return user;
    }

}
