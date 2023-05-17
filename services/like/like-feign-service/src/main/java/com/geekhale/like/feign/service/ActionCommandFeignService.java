package com.geekhale.like.feign.service;

import com.geekhalo.like.api.ActionCommandApi;
import com.geekhalo.like.app.ActionCommandApplicationService;
import com.geekhalo.like.domain.ActionTarget;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "orderInfo查询接口")
@RestController
@RequestMapping(ActionCommandApi.PATH)
public class ActionCommandFeignService implements ActionCommandApi {
    @Autowired
    private ActionCommandApplicationService commandApplicationService;

    @Override
    @PostMapping("like")
    public void like(@RequestParam Long userId, @RequestParam Integer targetType, @RequestParam Long targetId){
        this.commandApplicationService.like(userId, ActionTarget.apply(targetType, targetId));
    }

    @Override
    @PostMapping("unlike")
    public void unLike(@RequestParam Long userId, @RequestParam Integer targetType, @RequestParam Long targetId){
        this.commandApplicationService.unLike(userId, ActionTarget.apply(targetType, targetId));
    }

    @Override
    @PostMapping("dislike")
    public void dislike(@RequestParam Long user, @RequestParam Integer targetType, @RequestParam Long targetId){
        this.commandApplicationService.dislike(user, ActionTarget.apply(targetType, targetId));
    }

    @Override
    @PostMapping("unDislike")
    public void unDislike(@RequestParam Long userId, @RequestParam Integer targetType, @RequestParam Long targetId){
        this.commandApplicationService.unDislike(userId, ActionTarget.apply(targetType, targetId));
    }

}
