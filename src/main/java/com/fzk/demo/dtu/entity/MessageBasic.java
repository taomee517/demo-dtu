package com.fzk.demo.dtu.entity;

import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCounted;
import lombok.Data;

import java.util.Objects;

@Data
public class MessageBasic{
    public int funId;
    public byte[] content;
    public byte[] raw;

//    @Override
//    protected void deallocate() {
//        if(Objects.nonNull(content)){
//            content = null;
//        }
//        if(Objects.nonNull(raw)){
//            raw = null;
//        }
//    }
//
//    @Override
//    public ReferenceCounted touch(Object hint) {
//        return this;
//    }
}
