<template>
  <div class="admin-page">
    <div class="admin-container">
      <!-- 左侧菜单 -->
      <el-aside width="200px" class="admin-aside">
        <div class="admin-logo">
          <h2>后台管理</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="admin-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="statistics">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据统计</span>
          </el-menu-item>
          <el-menu-item index="users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="products">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="orders">
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
        </el-menu>
        <div class="logout-btn">
          <el-button type="danger" @click="handleLogout">退出登录</el-button>
        </div>
      </el-aside>

      <!-- 右侧内容区 -->
      <el-main class="admin-main">
        <!-- 数据统计 -->
        <div v-if="activeMenu === 'statistics'" class="statistics-section">
          <h3>数据统计</h3>
          <el-row :gutter="20">
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-icon user-icon">
                  <el-icon><User /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ statistics.userCount || 0 }}</div>
                  <div class="stat-label">用户总数</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-icon product-icon">
                  <el-icon><Goods /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ statistics.productCount || 0 }}</div>
                  <div class="stat-label">商品总数</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-icon order-icon">
                  <el-icon><Document /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ statistics.orderCount || 0 }}</div>
                  <div class="stat-label">订单总数</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-icon money-icon">
                  <el-icon><Money /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">¥{{ statistics.totalAmount || 0 }}</div>
                  <div class="stat-label">交易总额</div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <!-- 用户管理 -->
        <div v-if="activeMenu === 'users'" class="users-section">
          <h3>用户管理</h3>
          <el-table :data="users" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" width="150" />
            <el-table-column prop="nickname" label="昵称" width="150" />
            <el-table-column prop="email" label="邮箱" width="200" />
            <el-table-column prop="phone" label="手机号" width="150" />
            <el-table-column label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="注册时间" width="180" />
            <el-table-column label="操作" fixed="right" width="200">
              <template #default="{ row }">
                <el-button
                  v-if="row.status === 'NORMAL'"
                  size="small"
                  type="warning"
                  @click="handleUpdateUserStatus(row.id, 'FROZEN')"
                >冻结</el-button>
                <el-button
                  v-if="row.status === 'FROZEN'"
                  size="small"
                  type="success"
                  @click="handleUpdateUserStatus(row.id, 'NORMAL')"
                >解冻</el-button>
                <el-button
                  v-if="row.status !== 'BANNED'"
                  size="small"
                  type="danger"
                  @click="handleUpdateUserStatus(row.id, 'BANNED')"
                >封禁</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="userPage"
            :page-size="20"
            :total="userTotal"
            layout="total, prev, pager, next"
            @current-change="loadUsers"
          />
        </div>

        <!-- 商品管理 -->
        <div v-if="activeMenu === 'products'" class="products-section">
          <h3>商品管理</h3>
          <el-radio-group v-model="productStatus" @change="loadProducts" style="margin-bottom: 20px">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="PENDING">待审核</el-radio-button>
            <el-radio-button label="APPROVED">已通过</el-radio-button>
            <el-radio-button label="REJECTED">已拒绝</el-radio-button>
          </el-radio-group>
          <el-table :data="products" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column label="图片" width="100">
              <template #default="{ row }">
                <el-image :src="row.images?.[0]" style="width: 60px; height: 60px" fit="cover" />
              </template>
            </el-table-column>
            <el-table-column prop="title" label="商品名称" width="200" />
            <el-table-column prop="price" label="价格" width="120">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column prop="sellerName" label="卖家" width="150" />
            <el-table-column label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getProductStatusType(row.status)">{{ getProductStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="发布时间" width="180" />
            <el-table-column label="操作" fixed="right" width="200">
              <template #default="{ row }">
                <el-button
                  v-if="row.status === 'PENDING'"
                  size="small"
                  type="success"
                  @click="handleAuditProduct(row.id, 'APPROVED')"
                >通过</el-button>
                <el-button
                  v-if="row.status === 'PENDING'"
                  size="small"
                  type="danger"
                  @click="handleAuditProduct(row.id, 'REJECTED')"
                >拒绝</el-button>
                <el-button size="small" @click="viewProductDetail(row)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="productPage"
            :page-size="20"
            :total="productTotal"
            layout="total, prev, pager, next"
            @current-change="loadProducts"
          />
        </div>

        <!-- 订单管理 -->
        <div v-if="activeMenu === 'orders'" class="orders-section">
          <h3>订单管理</h3>
          <el-table :data="orders" style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="200" />
            <el-table-column prop="productTitle" label="商品名称" width="200" />
            <el-table-column prop="totalAmount" label="金额" width="120">
              <template #default="{ row }">¥{{ row.totalAmount }}</template>
            </el-table-column>
            <el-table-column prop="buyerName" label="买家" width="150" />
            <el-table-column prop="sellerName" label="卖家" width="150" />
            <el-table-column label="状态" width="120">
              <template #default="{ row }">
                <el-tag>{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" fixed="right" width="120">
              <template #default="{ row }">
                <el-button size="small" @click="viewOrderDetail(row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="orderPage"
            :page-size="20"
            :total="orderTotal"
            layout="total, prev, pager, next"
            @current-change="loadOrders"
          />
        </div>
      </el-main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DataAnalysis, User, Goods, Document, Money } from '@element-plus/icons-vue'
import { getStatistics, getUserList, updateUserStatus, getProductList, auditProduct } from '@/api/admin'

const router = useRouter()
const activeMenu = ref('statistics')

// 统计数据
const statistics = ref({})

// 用户管理
const users = ref([])
const userPage = ref(1)
const userTotal = ref(0)

// 商品管理
const products = ref([])
const productPage = ref(1)
const productTotal = ref(0)
const productStatus = ref('')

// 订单管理
const orders = ref([])
const orderPage = ref(1)
const orderTotal = ref(0)

// 菜单切换
const handleMenuSelect = (index) => {
  activeMenu.value = index
  if (index === 'statistics') {
    loadStatistics()
  } else if (index === 'users') {
    loadUsers()
  } else if (index === 'products') {
    loadProducts()
  } else if (index === 'orders') {
    loadOrders()
  }
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await getStatistics()
    statistics.value = res.data
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  }
}

// 加载用户列表
const loadUsers = async () => {
  try {
    const res = await getUserList(userPage.value)
    users.value = res.data.list || []
    userTotal.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  }
}

// 更新用户状态
const handleUpdateUserStatus = async (userId, status) => {
  try {
    await ElMessageBox.confirm(`确定要${getStatusText(status)}该用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateUserStatus(userId, status)
    ElMessage.success('操作成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 加载商品列表
const loadProducts = async () => {
  try {
    const res = await getProductList(productStatus.value || null, productPage.value)
    products.value = res.data.list || []
    productTotal.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载商品列表失败')
  }
}

// 审核商品
const handleAuditProduct = async (productId, status) => {
  try {
    await ElMessageBox.confirm(`确定要${status === 'APPROVED' ? '通过' : '拒绝'}该商品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await auditProduct(productId, status)
    ElMessage.success('操作成功')
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 查看商品详情
const viewProductDetail = (product) => {
  ElMessageBox.alert(`商品详情：${JSON.stringify(product, null, 2)}`, '商品详情')
}

// 加载订单列表
const loadOrders = async () => {
  // TODO: 实现订单列表API
  orders.value = []
  orderTotal.value = 0
}

// 查看订单详情
const viewOrderDetail = (order) => {
  ElMessageBox.alert(`订单详情：${JSON.stringify(order, null, 2)}`, '订单详情')
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    localStorage.removeItem('adminToken')
    router.push('/admin/login')
  } catch (error) {
    // 取消操作
  }
}

// 状态相关方法
const getStatusType = (status) => {
  const map = {
    NORMAL: 'success',
    FROZEN: 'warning',
    BANNED: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    NORMAL: '正常',
    FROZEN: '冻结',
    BANNED: '封禁'
  }
  return map[status] || status
}

const getProductStatusType = (status) => {
  const map = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger',
    SOLD: 'info'
  }
  return map[status] || 'info'
}

const getProductStatusText = (status) => {
  const map = {
    PENDING: '待审核',
    APPROVED: '已通过',
    REJECTED: '已拒绝',
    SOLD: '已售出'
  }
  return map[status] || status
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.admin-page {
  min-height: 100vh;
  background: #f0f2f5;
}

.admin-container {
  display: flex;
  height: 100vh;
}

.admin-aside {
  background: #001529;
  color: white;
}

.admin-logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.admin-logo h2 {
  margin: 0;
  color: white;
  font-size: 20px;
}

.admin-menu {
  border: none;
  background: #001529;
}

.admin-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.65);
}

.admin-menu :deep(.el-menu-item:hover) {
  color: white;
  background: rgba(255, 255, 255, 0.1);
}

.admin-menu :deep(.el-menu-item.is-active) {
  color: white;
  background: #1890ff;
}

.logout-btn {
  position: absolute;
  bottom: 20px;
  left: 20px;
  right: 20px;
}

.admin-main {
  flex: 1;
  overflow-y: auto;
  padding: 30px;
}

h3 {
  margin-bottom: 20px;
  font-size: 20px;
}

/* 统计卡片 */
.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  margin-right: 20px;
}

.user-icon {
  background: #e6f7ff;
  color: #1890ff;
}

.product-icon {
  background: #f6ffed;
  color: #52c41a;
}

.order-icon {
  background: #fff7e6;
  color: #fa8c16;
}

.money-icon {
  background: #fff1f0;
  color: #f5222d;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 表格分页 */
.el-pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
