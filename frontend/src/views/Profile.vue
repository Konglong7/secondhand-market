<template>
  <div class="profile-page">
    <Header />

    <div class="container">
      <div class="profile-layout">
        <!-- 左侧菜单 -->
        <div class="sidebar">
          <div class="user-card">
            <el-avatar :size="80" :src="userStore.userInfo?.avatar || defaultAvatar" />
            <div class="user-name">{{ userStore.userInfo?.username }}</div>
            <div class="user-credit">
              信用分：<span class="credit-score">{{ userStore.userInfo?.creditScore || 100 }}</span>
            </div>
          </div>

          <el-menu :default-active="activeMenu" @select="handleMenuSelect">
            <el-menu-item index="info">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
            <el-menu-item index="address">
              <el-icon><Location /></el-icon>
              <span>收货地址</span>
            </el-menu-item>
            <el-menu-item index="products">
              <el-icon><Goods /></el-icon>
              <span>我的发布</span>
            </el-menu-item>
            <el-menu-item index="favorites">
              <el-icon><Star /></el-icon>
              <span>我的收藏</span>
            </el-menu-item>
            <el-menu-item index="settings">
              <el-icon><Setting /></el-icon>
              <span>账户设置</span>
            </el-menu-item>
          </el-menu>
        </div>

        <!-- 右侧内容区 -->
        <div class="content-area">
          <!-- 个人信息 -->
          <el-card v-show="activeMenu === 'info'" class="content-card">
            <template #header>
              <div class="card-header">
                <span>个人信息</span>
              </div>
            </template>

            <el-form :model="userForm" label-width="100px">
              <el-form-item label="头像">
                <el-upload
                  class="avatar-uploader"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                >
                  <el-avatar :size="100" :src="userForm.avatar || defaultAvatar" />
                  <div class="upload-tip">点击上传头像</div>
                </el-upload>
              </el-form-item>
              <el-form-item label="用户名">
                <el-input v-model="userForm.username" disabled />
              </el-form-item>
              <el-form-item label="昵称">
                <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="userForm.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="userForm.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item label="信用分">
                <el-tag type="success" size="large">{{ userForm.creditScore || 100 }}</el-tag>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdateInfo">保存修改</el-button>
              </el-form-item>
            </el-form>
          </el-card>

          <!-- 收货地址 -->
          <el-card v-show="activeMenu === 'address'" class="content-card">
            <template #header>
              <div class="card-header">
                <span>收货地址</span>
                <el-button type="primary" @click="handleAddAddress">
                  <el-icon><Plus /></el-icon>
                  新增地址
                </el-button>
              </div>
            </template>

            <div v-loading="addressLoading" class="address-list">
              <div v-if="addressList.length === 0" class="empty-box">
                <el-empty description="暂无收货地址" />
              </div>

              <div
                v-for="addr in addressList"
                :key="addr.id"
                class="address-item"
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
                <div class="address-actions">
                  <el-button
                    v-if="!addr.isDefault"
                    type="primary"
                    link
                    @click="handleSetDefault(addr.id)"
                  >
                    设为默认
                  </el-button>
                  <el-button type="primary" link @click="handleEditAddress(addr)">
                    编辑
                  </el-button>
                  <el-button type="danger" link @click="handleDeleteAddress(addr.id)">
                    删除
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 我的发布 -->
          <el-card v-show="activeMenu === 'products'" class="content-card">
            <template #header>
              <div class="card-header">
                <span>我的发布</span>
                <el-button type="primary" @click="$router.push('/publish')">
                  <el-icon><Plus /></el-icon>
                  发布商品
                </el-button>
              </div>
            </template>

            <div v-loading="productsLoading" class="products-list">
              <div v-if="myProducts.length === 0" class="empty-box">
                <el-empty description="暂无发布商品" />
              </div>

              <div
                v-for="product in myProducts"
                :key="product.id"
                class="product-item"
              >
                <el-image
                  :src="product.images?.[0]"
                  class="product-image"
                  fit="cover"
                  @click="$router.push(`/product/${product.id}`)"
                />
                <div class="product-info">
                  <div class="product-title">{{ product.title }}</div>
                  <div class="product-price">¥{{ product.price }}</div>
                  <div class="product-status">
                    <el-tag :type="product.status === 1 ? 'success' : 'info'">
                      {{ product.status === 1 ? '在售' : '已下架' }}
                    </el-tag>
                    <span class="product-views">浏览 {{ product.views || 0 }}</span>
                  </div>
                </div>
                <div class="product-actions">
                  <el-button
                    v-if="product.status === 1"
                    type="warning"
                    size="small"
                    @click="handleOffShelf(product.id)"
                  >
                    下架
                  </el-button>
                  <el-button
                    v-else
                    type="success"
                    size="small"
                    @click="handleOnShelf(product.id)"
                  >
                    上架
                  </el-button>
                  <el-button
                    type="primary"
                    size="small"
                    @click="$router.push(`/publish?id=${product.id}`)"
                  >
                    编辑
                  </el-button>
                  <el-button
                    type="danger"
                    size="small"
                    @click="handleDeleteProduct(product.id)"
                  >
                    删除
                  </el-button>
                </div>
              </div>
            </div>

            <el-pagination
              v-if="productsTotal > 0"
              v-model:current-page="productsPage"
              v-model:page-size="productsPageSize"
              :total="productsTotal"
              layout="total, prev, pager, next"
              @current-change="loadMyProducts"
            />
          </el-card>

          <!-- 我的收藏 -->
          <el-card v-show="activeMenu === 'favorites'" class="content-card">
            <template #header>
              <div class="card-header">
                <span>我的收藏</span>
              </div>
            </template>

            <div v-loading="favoritesLoading" class="products-list">
              <div v-if="favoriteProducts.length === 0" class="empty-box">
                <el-empty description="暂无收藏商品" />
              </div>

              <div
                v-for="product in favoriteProducts"
                :key="product.id"
                class="product-item"
              >
                <el-image
                  :src="product.images?.[0]"
                  class="product-image"
                  fit="cover"
                  @click="$router.push(`/product/${product.id}`)"
                />
                <div class="product-info">
                  <div class="product-title">{{ product.title }}</div>
                  <div class="product-price">¥{{ product.price }}</div>
                  <div class="product-seller">卖家：{{ product.sellerName }}</div>
                </div>
                <div class="product-actions">
                  <el-button
                    type="danger"
                    size="small"
                    @click="handleCancelFavorite(product.id)"
                  >
                    取消收藏
                  </el-button>
                </div>
              </div>
            </div>

            <el-pagination
              v-if="favoritesTotal > 0"
              v-model:current-page="favoritesPage"
              v-model:page-size="favoritesPageSize"
              :total="favoritesTotal"
              layout="total, prev, pager, next"
              @current-change="loadFavorites"
            />
          </el-card>

          <!-- 账户设置 -->
          <el-card v-show="activeMenu === 'settings'" class="content-card">
            <template #header>
              <div class="card-header">
                <span>账户设置</span>
              </div>
            </template>

            <el-form :model="passwordForm" label-width="120px">
              <el-form-item label="原密码">
                <el-input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  placeholder="请输入原密码"
                  show-password
                />
              </el-form-item>
              <el-form-item label="新密码">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码"
                  show-password
                />
              </el-form-item>
              <el-form-item label="确认新密码">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleChangePassword">
                  修改密码
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 地址编辑弹窗 -->
    <el-dialog
      v-model="showAddressDialog"
      :title="addressForm.id ? '编辑地址' : '新增地址'"
      width="600px"
    >
      <el-form :model="addressForm" label-width="100px">
        <el-form-item label="收货人">
          <el-input v-model="addressForm.receiver" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="addressForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="省份">
          <el-input v-model="addressForm.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="addressForm.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县">
          <el-input v-model="addressForm.district" placeholder="请输入区县" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input
            v-model="addressForm.detail"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addressForm.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddressDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User,
  Location,
  Goods,
  Star,
  Setting,
  Plus
} from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import { getUserInfo } from '@/api/auth'
import { updateUserInfo as updateUserInfoApi, changePassword as changePasswordApi } from '@/api/user'
import {
  getAddressList,
  addAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress
} from '@/api/address'
import { getMyProducts, deleteProduct, onShelfProduct, offShelfProduct } from '@/api/product'
import { getFavorites, cancelFavorite } from '@/api/favorite'

const router = useRouter()
const userStore = useUserStore()

const activeMenu = ref('info')
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 个人信息
const userForm = ref({
  username: '',
  nickname: '',
  phone: '',
  email: '',
  avatar: '',
  creditScore: 100
})

// 地址相关
const addressLoading = ref(false)
const addressList = ref([])
const showAddressDialog = ref(false)
const addressForm = ref({
  id: null,
  receiver: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false
})

// 我的发布
const productsLoading = ref(false)
const myProducts = ref([])
const productsPage = ref(1)
const productsPageSize = ref(10)
const productsTotal = ref(0)

// 我的收藏
const favoritesLoading = ref(false)
const favoriteProducts = ref([])
const favoritesPage = ref(1)
const favoritesPageSize = ref(10)
const favoritesTotal = ref(0)

// 账户设置
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    userForm.value = { ...res.data }
  } catch (error) {
    ElMessage.error('加载用户信息失败')
  }
}

// 更新用户信息
const handleUpdateInfo = async () => {
  try {
    await updateUserInfoApi({
      nickname: userForm.value.nickname,
      avatar: userForm.value.avatar
    })
    ElMessage.success('保存成功')
    loadUserInfo()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 头像上传
const handleAvatarSuccess = (response) => {
  userForm.value.avatar = response.data.url
  ElMessage.success('头像上传成功')
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

// 加载地址列表
const loadAddressList = async () => {
  addressLoading.value = true
  try {
    const res = await getAddressList()
    addressList.value = res.data || []
  } catch (error) {
    ElMessage.error('加载地址列表失败')
  } finally {
    addressLoading.value = false
  }
}

// 新增地址
const handleAddAddress = () => {
  addressForm.value = {
    id: null,
    receiver: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: false
  }
  showAddressDialog.value = true
}

// 编辑地址
const handleEditAddress = (addr) => {
  addressForm.value = { ...addr }
  showAddressDialog.value = true
}

// 保存地址
const handleSaveAddress = async () => {
  if (!addressForm.value.receiver || !addressForm.value.phone || !addressForm.value.detail) {
    ElMessage.warning('请填写完整的地址信息')
    return
  }

  try {
    if (addressForm.value.id) {
      await updateAddress(addressForm.value.id, addressForm.value)
      ElMessage.success('地址更新成功')
    } else {
      await addAddress(addressForm.value)
      ElMessage.success('地址添加成功')
    }
    showAddressDialog.value = false
    loadAddressList()
  } catch (error) {
    ElMessage.error('保存地址失败')
  }
}

// 删除地址
const handleDeleteAddress = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAddress(id)
    ElMessage.success('地址删除成功')
    loadAddressList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除地址失败')
    }
  }
}

// 设置默认地址
const handleSetDefault = async (id) => {
  try {
    await setDefaultAddress(id)
    ElMessage.success('设置成功')
    loadAddressList()
  } catch (error) {
    ElMessage.error('设置失败')
  }
}

// 加载我的发布
const loadMyProducts = async () => {
  productsLoading.value = true
  try {
    const res = await getMyProducts(productsPage.value, productsPageSize.value)
    myProducts.value = res.data.list || []
    productsTotal.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载商品列表失败')
  } finally {
    productsLoading.value = false
  }
}

// 上架商品
const handleOnShelf = async (id) => {
  try {
    await onShelfProduct(id)
    ElMessage.success('商品已上架')
    loadMyProducts()
  } catch (error) {
    ElMessage.error('上架失败')
  }
}

// 下架商品
const handleOffShelf = async (id) => {
  try {
    await offShelfProduct(id)
    ElMessage.success('商品已下架')
    loadMyProducts()
  } catch (error) {
    ElMessage.error('下架失败')
  }
}

// 删除商品
const handleDeleteProduct = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteProduct(id)
    ElMessage.success('商品删除成功')
    loadMyProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除商品失败')
    }
  }
}

// 加载收藏列表
const loadFavorites = async () => {
  favoritesLoading.value = true
  try {
    const res = await getFavorites(favoritesPage.value, favoritesPageSize.value)
    favoriteProducts.value = res.data.list || []
    favoritesTotal.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载收藏列表失败')
  } finally {
    favoritesLoading.value = false
  }
}

// 取消收藏
const handleCancelFavorite = async (productId) => {
  try {
    await cancelFavorite(productId)
    ElMessage.success('已取消收藏')
    loadFavorites()
  } catch (error) {
    ElMessage.error('取消收藏失败')
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword) {
    ElMessage.warning('请填写完整的密码信息')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }
  if (passwordForm.value.newPassword.length < 6) {
    ElMessage.warning('新密码长度不能少于6位')
    return
  }

  try {
    await changePasswordApi(passwordForm.value.oldPassword, passwordForm.value.newPassword)
    ElMessage.success('密码修改成功')
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  } catch (error) {
    ElMessage.error('密码修改失败')
  }
}

// 菜单选择
const handleMenuSelect = (index) => {
  activeMenu.value = index

  // 加载对应数据
  switch (index) {
    case 'address':
      loadAddressList()
      break
    case 'products':
      loadMyProducts()
      break
    case 'favorites':
      loadFavorites()
      break
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.profile-layout {
  display: flex;
  gap: 20px;
}

.sidebar {
  width: 240px;
  flex-shrink: 0;
}

.user-card {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  text-align: center;
  margin-bottom: 20px;
}

.user-name {
  font-size: 18px;
  font-weight: bold;
  margin-top: 12px;
}

.user-credit {
  font-size: 14px;
  color: #606266;
  margin-top: 8px;
}

.credit-score {
  color: #67c23a;
  font-weight: bold;
  font-size: 16px;
}

.content-area {
  flex: 1;
}

.content-card {
  min-height: 500px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.avatar-uploader {
  text-align: center;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.empty-box {
  padding: 60px 0;
}

.address-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s;
}

.address-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
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

.address-actions {
  display: flex;
  gap: 8px;
}

.products-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 400px;
}

.product-item {
  display: flex;
  gap: 16px;
  align-items: center;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s;
}

.product-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.product-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  cursor: pointer;
}

.product-info {
  flex: 1;
}

.product-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  cursor: pointer;
}

.product-title:hover {
  color: #409eff;
}

.product-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 8px;
}

.product-status {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-views {
  font-size: 14px;
  color: #909399;
}

.product-seller {
  font-size: 14px;
  color: #909399;
}

.product-actions {
  display: flex;
  gap: 8px;
}

@media (max-width: 768px) {
  .profile-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
  }

  .product-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .product-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
