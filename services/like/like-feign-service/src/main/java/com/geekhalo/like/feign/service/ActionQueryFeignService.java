package com.geekhalo.like.feign.service;

import com.geekhalo.like.api.ActionQueryApi;
import com.geekhalo.like.api.ActionVO;
import com.geekhalo.like.app.ActionQueryApplicationService;
import com.geekhalo.like.domain.AbstractAction;
import com.geekhalo.like.domain.dislike.DislikeAction;
import com.geekhalo.like.domain.like.LikeAction;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Api(tags = "查询接口")
@RestController
@RequestMapping(ActionQueryApi.PATH)
public class ActionQueryFeignService implements ActionQueryApi {
    @Autowired
    private ActionQueryApplicationService actionQueryApplicationService;

    @Override
    public List<ActionVO> getLikeByUserAndType(Long userId, String type) {
        List<LikeAction> likeActions = this.actionQueryApplicationService.getLikeByUserAndType(userId, type);
        return likeActions.stream()
                .map(this::toActionVO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @Override
    public List<ActionVO> getDislikeByUserAndType(Long userId, String type) {
        List<DislikeAction> actions = this.actionQueryApplicationService.getDislikeByUserAndType(userId, type);
        return actions.stream()
                .map(this::toActionVO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ActionVO toActionVO(AbstractAction action) {
        if (action == null){
            return null;
        }
        ActionVO actionVO = new ActionVO();
        actionVO.setTargetType(action.getTarget().getType());
        actionVO.setTargetId(action.getTarget().getId());
        actionVO.setUserId(action.getUser().getUserId());
        actionVO.setValid(action.isValid());
        return actionVO;
    }
}
