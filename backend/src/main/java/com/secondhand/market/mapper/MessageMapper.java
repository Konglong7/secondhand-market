package com.secondhand.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.market.entity.Message;
import com.secondhand.market.vo.ConversationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    @Select("SELECT " +
            "CASE WHEN m.from_user_id = #{userId} THEN m.to_user_id ELSE m.from_user_id END as userId, " +
            "u.username, u.nickname, u.avatar, " +
            "m.content as lastMessage, " +
            "m.message_type as lastMessageType, " +
            "m.create_time as lastMessageTime, " +
            "(SELECT COUNT(*) FROM message WHERE from_user_id = " +
            "CASE WHEN m.from_user_id = #{userId} THEN m.to_user_id ELSE m.from_user_id END " +
            "AND to_user_id = #{userId} AND is_read = 0) as unreadCount " +
            "FROM message m " +
            "INNER JOIN user u ON u.id = CASE WHEN m.from_user_id = #{userId} THEN m.to_user_id ELSE m.from_user_id END " +
            "WHERE m.id IN ( " +
            "SELECT MAX(id) FROM message " +
            "WHERE from_user_id = #{userId} OR to_user_id = #{userId} " +
            "GROUP BY CASE WHEN from_user_id = #{userId} THEN to_user_id ELSE from_user_id END " +
            ") " +
            "ORDER BY m.create_time DESC")
    List<ConversationVO> getConversationList(@Param("userId") Long userId);

    @Update("UPDATE message SET is_read = 1 " +
            "WHERE from_user_id = #{fromUserId} AND to_user_id = #{toUserId} AND is_read = 0")
    int markAsRead(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);
}
