package com.geekhalo.like.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

public interface ActionCommandApi {
    String PATH = "feignService/action/command";


    @PostMapping("like")
    void like(@RequestBody @Valid  ActionCommandParam param);

    @PostMapping("unlike")
    void unLike(@RequestBody @Valid ActionCommandParam param);

    @PostMapping("dislike")
    void dislike(@RequestBody @Valid ActionCommandParam param);

    @PostMapping("unDislike")
    void unDislike(@RequestBody @Valid ActionCommandParam param);
}
