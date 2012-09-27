package com.example.autotest;

import android.util.Log;

public class MessageBody {

    private static final String LOG_TAG = "messagebody";
    /* The MessageBody transfered from Java to C.
     * The C struct is as follow.
    struct message{
        enum MSG_ID msg;

        u32 msg_len;
        u8 payload[0];
    };
    struct message_image{
        u32 type; //
        u32 bits;

        u32 x;
        u32 y;
        u32 width;
        u32 height;

        u8 data[0];
    };
    
    enum VIDEO_TYPE{
    VIDEO_TYPE_RGB1555 = 0,
    VIDEO_TYPE_RGB565,
    VIDEO_TYPE_RGG8888,
    VIDEO_TYPE_YCBCR420,
    VIDEO_TYPE_YCBCR422,
    VIDEO_TYPE_RESERVED,
};
    */
    
    //MSG_ID
    private static final int MSG_ID_DISCV = 0;
    private static final int MSG_ID_CONNECT = 1;
    private static final int MSG_ID_IMAGE = 2;
    private static final int MSG_ID_SYSTEM_INFO = 3;
    private static final int MSG_ID_AP_CONFIG = 4;
    private static final int MSG_ID_EVENT = 5;
    private static final int MSG_ID_END = 6;
    
    //VIDEO_TYPE
    private static final int VIDEO_TYPE_RGB1555 = 0;
    private static final int VIDEO_TYPE_RGB565 = 1;
    private static final int VIDEO_TYPE_RGG8888 = 2;
    private static final int VIDEO_TYPE_YCBCR420 = 3;
    private static final int VIDEO_TYPE_YCBCR422 = 4;
    private static final int VIDEO_TYPE_RESERVED = 5;

    private int msg;
    private int msg_len;
    
    private int type;
    private int bits;
    
    private int x;
    private int y;
    private int width;
    private int height;
    
    private byte[] buf;
    
    public MessageBody(int left, int top, int w, int h, int buflen, byte[] os){        
        //
        msg = MSG_ID_IMAGE;        
        type = 5;        
        bits = 16;
        
        x = left;        
        y = top;        
        width = w;        
        height = h;
        
        //msg->msg_len = sizeof(struct message_image) + (image->width * image->height * 2);
        //We do not include data[0] in struct message_image. 
        //So we use 20 as sizeof(struct message_image) not 24.
        msg_len = 24 + buflen;
        
        buf = new byte[buflen + 32];
        byte[] tmp = Utils.toLHByte(msg);
        System.arraycopy(tmp, 0, buf, 0, tmp.length);
        tmp = Utils.toLHByte(msg_len);
        System.arraycopy(tmp, 0, buf, 4, tmp.length);
        tmp = Utils.toLHByte(type);
        System.arraycopy(tmp, 0, buf, 8, tmp.length);
        tmp = Utils.toLHByte(bits);
        System.arraycopy(tmp, 0, buf, 12, tmp.length);
        tmp = Utils.toLHByte(x);
        System.arraycopy(tmp, 0, buf, 16, tmp.length);
        tmp = Utils.toLHByte(y);
        System.arraycopy(tmp, 0, buf, 20, tmp.length);
        tmp = Utils.toLHByte(width);
        System.arraycopy(tmp, 0, buf, 24, tmp.length);
        tmp = Utils.toLHByte(height);
        System.arraycopy(tmp, 0, buf, 28, tmp.length);
        System.arraycopy(os, 0, buf, 32, os.length);
    }

    public byte[] getBuf(){        
        return buf;
    }
    
    //Another function
    public MessageBody(int w, int h){
        msg = MSG_ID_IMAGE;        
        type = VIDEO_TYPE_RGB565;        
        bits = 16;
        
        x = 0;        
        y = 0;        
        width = w;        
        height = h;
        
        msg_len = 20 + w * h * 2;
    }
    
    public int getMsg(){
        return Utils.toLHInt(msg);
    }

    public int getMsgLen(){
        return Utils.toLHInt(msg_len);
    }
    
    public int getType(){
        return Utils.toLHInt(type);
    }
    
    public int getBits(){
        return Utils.toLHInt(bits);
    }
    
    public int getX(){
        return Utils.toLHInt(x);
    }
    
    public int getY(){
        return Utils.toLHInt(y);
    }
    
    public int getWidth(){
        return Utils.toLHInt(width);
    }
    
    public int getHeight(){
        return Utils.toLHInt(height);
    }
    
}
