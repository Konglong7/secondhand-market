<template>
  <div class="orders-page">
    <Header />

    <div class="container">
      <el-card>
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="我的购买" name="buyer">
            <div class="status-tabs">
              <div
                v-for="status in statusList"
                :key="status.value"
                class="status-item"
                :class="{ active: currentStatus === status.value }"
                @click="changeStatus(status.value)"
              >
                {{ status.label }}
              </div>
            </div>

            <div v-loading="loading" class="orders-list">
              <div v-if="orderList.length === 0" class="empty-box">
                <el-empty description="暂无订单" />
              </div>

              <div v-for="order in orderList" :key="order.id" class="order-item">
                <div class="order-header">
                  <span class="order-no">订单号：{{ order.orderNo }}</span>
                  <span class="order-time">{{ order.createTime }}</span>
                  <el-tag :type="getStatusType(order.status)">
                    {{ getStatusText(order.status) }}
                  </el-tag>
                </div>

                <div class="order-content">
                  <el-image
                    :src="order.productImage"
                    class="product-image"
                    fit="cover"
                  />
                  <div class="product-info">
                    <div class="product-title">{{ order.productTitle }}</div>
                    <div class="product-desc">{{ order.productDesc }}</div>
                  </div>
                  <div class="order-price">¥{{ order.totalPrice }}</div>
                </div>

                <div class="order-footer">
                  <div class="order-actions">
                    <el-button
                      v-if="order.status === 'PENDING_PAYMENT'"
                      type="danger"
                      size="small"
                      @click="handlePay(order)"
                    >
                      立即付款
                    </el-button>
                    <el-button
                      v-if="order.status === 'PENDING_PAYMENT'"
                      size="small"
                      @click="handleCancel(order.id)"
                    >
                      取消订单
                    </el-button>
                    <el-button
                      v-if="order.status === 'SHIPPED'"
                      type="primary"
                      size="small"
                      @click="handleConfirmReceive(order.id)"
                    >
                      确认收货
                    </el-button>
                    <el-button
                      v-if="order.status === 'SHIPPED' || order.status === 'COMPLETED'"
                      size="small"
                      @click="handleViewExpress(order)"
                    >
                      查看物流
                    </el-button>
                    <el-button
                      v-if="order.status === 'COMPLETED'"
                      size="small"
                      @click="handleEvaluate(order)"
                    >
                      评价
                    </el-button>
                    <el-button size="small" @click="handleViewDetail(order.id)">
                      订单详情
                    </el-button>
                  </div>
                </div>
              </div>
            </div>

            <el-pagination
              v-if="total > 0"
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              layout="total, prev, pager, next"
              @current-change="loadOrders"
            />
          </el-tab-pane>

          <el-tab-pane label="我的销售" name="seller">
            <div class="status-tabs">
              <div
                v-for="status in statusList"
                :key="status.value"
                class="status-item"
                :class="{ active: currentStatus === status.value }"
                @click="changeStatus(status.value)"
              >
                {{ status.label }}
              </div>
            </div>

            <div v-loading="loading" class="orders-list">
              <div v-if="orderList.length === 0" class="empty-box">
                <el-empty description="暂无订单" />
              </div>

              <div v-for="order in orderList" :key="order.id" class="order-item">
                <div class="order-header">
                  <span class="order-no">订单号：{{ order.orderNo }}</span>
                  <span class="order-time">{{ order.createTime }}</span>
                  <el-tag :type="getStatusType(order.status)">
                    {{ getStatusText(order.status) }}
                  </el-tag>
                </div>

                <div class="order-content">
                  <el-image
                    :src="order.productImage"
                    class="product-image"
                    fit="cover"
                  />
                  <div class="product-info">
                    <div class="product-title">{{ order.productTitle }}</div>
                    <div class="product-desc">买家：{{ order.buyerName }}</div>
                  </div>
                  <div class="order-price">¥{{ order.totalPrice }}</div>
                </div>

                <div class="order-footer">
                  <div class="order-actions">
                    <el-button
                      v-if="order.status === 'PAID'"
                      type="primary"
                      size="small"
                      @click="handleShip(order)"
                    >
                      发货
                    </el-button>
                    <el-button size="small" @click="handleViewDetail(order.id)">
                      订单详情
                    </el-button>
                  </div>
                </div>
              </div>
            </div>

            <el-pagination
              v-if="total > 0"
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              layout="total, prev, pager, next"
              @current-change="loadOrders"
            />
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>

    <!-- 支付弹窗 -->
    <el-dialog v-model="showPayDialog" title="选择支付方式" width="400px">
      <div class="pay-methods">
        <div
          class="pay-method"
          :class="{ selected: paymentType === 'alipay' }"
          @click="paymentType = 'alipay'"
        >
          <el-icon :size="40" color="#1677ff"><CreditCard /></el-icon>
          <span>支付宝</span>
        </div>
        <div
          class="pay-method"
          :class="{ selected: paymentType === 'wechat' }"
          @click="paymentType = 'wechat'"
        >
          <el-icon :size="40" color="#07c160"><ChatDotRound /></el-icon>
          <span>微信支付</span>
        </div>
      </div>
      <div class="pay-amount">
        支付金额：<span class="amount">¥{{ currentOrder?.totalPrice }}</span>
      </div>
      <template #footer>
        <el-button @click="showPayDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmPay">确认支付</el-button>
      </template>
    </el-dialog>

    <!-- 发货弹窗 -->
    <el-dialog v-model="showShipDialog" title="填写物流信息" width="500px">
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="快递公司">
          <el-select v-model="shipForm.expressCompany" placeholder="请选择快递公司">
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="圆通速递" value="圆通速递" />
            <el-option label="申通快递" value="申通快递" />
            <el-option label="韵达快递" value="韵达快递" />
            <el-option label="邮政EMS" value="邮政EMS" />
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="shipForm.expressNo" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showShipDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CreditCard, ChatDotRound } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import {
  getBuyerOrders,
  getSellerOrders,
  payOrder,
  shipOrder,
  confirmReceive,
  cancelOrder
} from '@/api/order'

const router = useRouter()

const activeTab = ref('buyer')
const currentStatus = ref('')
const loading = ref(false)
const orderList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const statusList = [
  { label: '全部', value: '' },
  { label: '待付款', value: 'PENDING_PAYMENT' },
  { label: '待发货', value: 'PAID' },
  { label: '待收货', value: 'SHIPPED' },
  { label: '已完成', value: 'COMPLETED' }
]

// 支付相关
const showPayDialog = ref(false)
const paymentType = ref('alipay')
const currentOrder = ref(null)

// 发货相关
const showShipDialog = ref(false)
const shipForm = ref({
  expressCompany: '',
  expressNo: ''
})

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const apiFunc = activeTab.value === 'buyer' ? getBuyerOrders : getSellerOrders
    const res = await apiFunc(currentStatus.value, currentPage.value, pageSize.value)
    orderList.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

// 切换Tab
const handleTabChange = () => {
  currentStatus.value = ''
  currentPage.value = 1
  loadOrders()
}

// 切换状态
const changeStatus = (status) => {
  currentStatus.value = status
  currentPage.value = 1
  loadOrders()
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    PENDING_PAYMENT: 'warning',
    PAID: 'info',
    SHIPPED: 'primary',
    COMPLETED: 'success',
    CANCELLED: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    PENDING_PAYMENT: '待付款',
    PAID: '待发货',
    SHIPPED: '待收货',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return textMap[status] || status
}

// 支付订单
const handlePay = (order) => {
  currentOrder.value = order
  paymentType.value = 'alipay'
  showPayDialog.value = true
}

// 确认支付
const confirmPay = async () => {
  try {
    await payOrder(currentOrder.value.id, paymentType.value)
    ElMessage.success('支付成功')
    showPayDialog.value = false
    loadOrders()
  } catch (error) {
    ElMessage.error('支付失败')
  }
}

// 取消订单
const handleCancel = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelOrder(orderId)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

// 确认收货
const handleConfirmReceive = async (orderId) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await confirmReceive(orderId)
    ElMessage.success('确认收货成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认收货失败')
    }
  }
}

// 发货
const handleShip = (order) => {
  currentOrder.value = order
  shipForm.value = {
    expressCompany: '',
    expressNo: ''
  }
  showShipDialog.value = true
}

// 确认发货
const confirmShip = async () => {
  if (!shipForm.value.expressCompany || !shipForm.value.expressNo) {
    ElMessage.warning('请填写完整的物流信息')
    return
  }
  try {
    await shipOrder(
      currentOrder.value.id,
      shipForm.value.expressCompany,
      shipForm.value.expressNo
    )
    ElMessage.success('发货成功')
    showShipDialog.value = false
    loadOrders()
  } catch (error) {
    ElMessage.error('发货失败')
  }
}

// 查看物流
const handleViewExpress = (order) => {
  ElMessageBox.alert(
    `快递公司：${order.expressCompany}\n快递单号：${order.expressNo}`,
    '物流信息',
    { confirmButtonText: '确定' }
  )
}

// 评价
const handleEvaluate = (order) => {
  ElMessage.info('评价功能开发中')
}

// 查看详情
const handleViewDetail = (orderId) => {
  router.push(`/order-detail/${orderId}`)
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.status-tabs {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.status-item {
  padding: 12px 20px;
  cursor: pointer;
  color: #606266;
  transition: all 0.3s;
  border-bottom: 2px solid transparent;
}

.status-item:hover {
  color: #409eff;
}

.status-item.active {
  color: #409eff;
  border-bottom-color: #409eff;
  font-weight: bold;
}

.orders-list {
  min-height: 400px;
}

.empty-box {
  padding: 60px 0;
}

.order-item {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  border: 1px solid #e4e7ed;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 12px;
}

.order-no {
  font-size: 14px;
  color: #606266;
}

.order-time {
  font-size: 14px;
  color: #909399;
}

.order-content {
  display: flex;
  gap: 16px;
  align-items: center;
  padding: 12px 0;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
}

.product-info {
  flex: 1;
}

.product-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}

.product-desc {
  font-size: 14px;
  color: #909399;
}

.order-price {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 12px;
  border-top: 1px solid #e4e7ed;
}

.order-actions {
  display: flex;
  gap: 8px;
}

.pay-methods {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin: 20px 0;
}

.pay-method {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  width: 120px;
}

.pay-method:hover {
  border-color: #409eff;
}

.pay-method.selected {
  border-color: #409eff;
  background: #f0f9ff;
}

.pay-amount {
  text-align: center;
  font-size: 16px;
  margin-top: 20px;
}

.amount {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}
</style>
