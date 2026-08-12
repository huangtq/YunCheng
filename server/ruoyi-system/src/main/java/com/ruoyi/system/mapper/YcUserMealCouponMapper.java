package com.ruoyi.system.mapper;
import java.util.List;import org.apache.ibatis.annotations.Param;import com.ruoyi.system.domain.YcUserMealCoupon;
public interface YcUserMealCouponMapper {YcUserMealCoupon selectById(Long couponId);YcUserMealCoupon selectByTicketAndOrder(@Param("ticketId")Long ticketId,@Param("orderId")Long orderId);List<YcUserMealCoupon> selectByOrderId(Long orderId);int insert(YcUserMealCoupon coupon);int redeemIfAvailable(Long couponId);int revokeIfRedeemed(Long couponId);}
