<template>
  <div class="order-confirm-page">
    <Header />

    <div class="container">
      <el-card class="section-card">
        <template #header>
          <div class="card-header">
            <el-icon><Location /></el-icon>
            <span>收货地址</span>
            <el-button type="primary" link @click="showAddressDialog = true">
              管理地址
            </el-button>
          </div>
        </template>

        <div v-if="selectedAddress" class="address-item selected">
          <div class="address-info">
            <div class="address-header">
              <span class="receiver">{{ selectedAddress.receiver }}</span>
              <span class="phone">{{ selectedAddress.phone }}</span>
              <el-tag v-if="selectedAddress.isDefault" type="danger" size="small">默认</el-tag>
            </div>
            <div class="address-detail">
              {{ selectedAddress.province }} {{ selectedAddress.city }}
              {{ selectedAddress.district }} {{ selectedAddress.detail }}
            </div>
          </div>
          <el-button type="primary" link @click="showAddressDialog = true">
            更换地址
          </el-button>
        </div>

        <el-empty v-else description="请选择收货地址">
          <el-button type="primary" @click="showAddressDialog = true">
            添加地址
          </el-button>
        </el-empty>
      </el-card>

      <el-card class="section-card">
        <template #header>
          <div class="card-header">
            <el-icon><ShoppingCart /></el-icon>
            <span>商品信息</span>
          </div>
        </template>

        <div v-if="product" class="product-item">
          <el-image :src="product.images?.[0]" class="product-image" fit="cover" />
          <div class="product-info">
            <div class="product-title">{{ product.title }}</div>
            <div class="product-desc">{{ product.description }}</div>
          </div>
          <div class="product-price">¥{{ product.price }}</div>
        </div>
      </el-card>

      <el-card class="section-card">
        <template #header>
          <div class="card-header">
            <el-icon><ChatLineRound /></el-icon>
            <span>买家留言</span>
          </div>
        </template>

        <el-input
          v-model="buyerMessage"
          type="textarea"
          :rows="3"
          placeholder="选填，给卖家留言"
          maxlength="200"
          show-word-limit
        />
      </el-card>

      <el-card class="section-card">
        <div class="order-summary">
          <div class="summary-item">
            <span>商品金额</span>
            <span class="price">¥{{ product?.price || 0 }}</span>
          </div>
          <div class="summary-item total">
            <span>实付款</span>
            <span class="price">¥{{ product?.price || 0 }}</span>
          </div>
        </div>
      </el-card>

      <div class="submit-bar">
        <div class="total-info">
          <span>应付总额：</span>
          <span class="total-price">¥{{ product?.price || 0 }}</span>
        </div>
        <el-button
          type="danger"
          size="large"
          :loading="submitting"
          @click="handleSubmit"
        >
          提交订单
        </el-button>
      </div>
    </div>

    <!-- 地址选择弹窗 -->
    <el-dialog
      v-model="showAddressDialog"
      title="选择收货地址"
      width="600px"
    >
      <div class="address-list">
        <div
          v-for="addr in addressList"
          :key="addr.id"
          class="address-item"
          :class="{ selected: selectedAddress?.id === addr.id }"
          @click="selectAddress(addr)"
        >
          <div class="address-info">
            <div class="address-header">
              <span class="receiver">{{ addr.receiver }}</span>
              <span class="phone">{{ addr.phone }}</span>
              <el-tag v-if="addr.isDefault" type="danger" size="small">默认</el-tag>
            </div>
            <div class="address-detail">
              {{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detail }}
            </div>
          </div>
          <el-icon v-if="selectedAddress?.id === addr.id" class="check-icon" color="#67c23a">
            <CircleCheck />
          </el-icon>
        </div>
      </div>

      <template #footer>
        <el-button @click="showAddressDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmAddress">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Location,
  ShoppingCart,
  ChatLineRound,
  CircleCheck
} from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import { getProductDetail } from '@/api/product'
import { getAddressList } from '@/api/address'
import { createOrder } from '@/api/order'

const route = useRoute()
const router = useRouter()

const product = ref(null)
const addressList = ref([])
const selectedAddress = ref(null)
const buyerMessage = ref('')
const showAddressDialog = ref(false)
const submitting = ref(false)

// 加载商品信息
const loadProduct = async () => {
  try {
    const productId = route.query.productId
    if (!productId) {
      ElMessage.error('商品信息不存在')
      router.back()
      return
    }
    const res = await getProductDetail(productId)
    product.value = res.data
  } catch (error) {
    ElMessage.error('加载商品信息失败')
  }
}

// 加载地址列表
const loadAddressList = async () => {
  try {
    const res = await getAddressList()
    addressList.value = res.data || []
    // 默认选中默认地址
    selectedAddress.value = addressList.value.find(addr => addr.isDefault) || addressList.value[0]
  } catch (error) {
    ElMessage.error('加载地址列表失败')
  }
}

// 选择地址
const selectAddress = (addr) => {
  selectedAddress.value = addr
}

// 确认地址
const confirmAddress = () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  showAddressDialog.value = false
}

// 提交订单
const handleSubmit = async () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    showAddressDialog.value = true
    return
  }

  submitting.value = true
  try {
    const orderData = {
      productId: product.value.id,
      addressId: selectedAddress.value.id,
      buyerMessage: buyerMessage.value
    }
    const res = await createOrder(orderData)
    ElMessage.success('订单创建成功')
    // 跳转到支付页面
    router.push({
      path: '/orders',
      query: { orderId: res.data.id, action: 'pay' }
    })
  } catch (error) {
    ElMessage.error('创建订单失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadProduct()
  loadAddressList()
})
</script>

<style scoped>
.order-confirm-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.section-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
}

.address-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 12px;
}

.address-item:hover {
  border-color: #409eff;
}

.address-item.selected {
  border-color: #67c23a;
  background: #f0f9ff;
}

.address-info {
  flex: 1;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.receiver {
  font-weight: bold;
  font-size: 16px;
}

.phone {
  color: #606266;
}

.address-detail {
  color: #606266;
  line-height: 1.6;
}

.check-icon {
  font-size: 24px;
}

.product-item {
  display: flex;
  gap: 16px;
  align-items: center;
}

.product-image {
  width: 100px;
  height: 100px;
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
  color: #909399;
  font-size: 14px;
}

.product-price {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
}

.order-summary {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.summary-item.total {
  font-size: 16px;
  font-weight: bold;
  padding-top: 12px;
  border-top: 1px solid #e4e7ed;
}

.price {
  color: #f56c6c;
}

.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 16px 20px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
  z-index: 100;
}

.total-info {
  font-size: 16px;
}

.total-price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}

.address-list {
  max-height: 400px;
  overflow-y: auto;
}
</style>
