<template>
  <div class="home-page">
    <Header />

    <div class="main-content">
      <!-- 分类导航 -->
      <div class="category-nav">
        <div class="category-scroll">
          <el-button
            v-for="category in categories"
            :key="category.id"
            :type="selectedCategory === category.id ? 'primary' : ''"
            @click="selectCategory(category.id)"
            class="category-btn"
          >
            {{ category.name }}
          </el-button>
        </div>
      </div>

      <!-- 商品瀑布流 -->
      <div class="product-container">
        <el-row :gutter="20">
          <el-col
            :xs="12"
            :sm="8"
            :md="6"
            :lg="6"
            v-for="product in productList"
            :key="product.id"
          >
            <el-card
              :body-style="{ padding: '0px' }"
              class="product-card"
              @click="goToDetail(product.id)"
            >
              <el-image
                :src="product.images?.[0] || defaultImage"
                :lazy="true"
                class="product-image"
                fit="cover"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="product-info">
                <div class="product-title">{{ product.title }}</div>
                <div class="product-price">¥{{ product.price }}</div>
                <div class="product-meta">
                  <span class="location">
                    <el-icon><Location /></el-icon>
                    {{ product.location || '未知' }}
                  </span>
                  <span class="views">
                    <el-icon><View /></el-icon>
                    {{ product.views || 0 }}
                  </span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 空状态 -->
        <el-empty v-if="!loading && productList.length === 0" description="暂无商品" />

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-icon class="is-loading"><Loading /></el-icon>
        </div>
      </div>

      <!-- 分页器 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[12, 24, 36, 48]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getProductList } from '@/api/product'
import { getCategoryList } from '@/api/category'
import Header from '@/components/Header.vue'
import { Picture, Location, View, Loading } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const categories = ref([{ id: null, name: '全部' }])
const selectedCategory = ref(null)
const productList = ref([])
const loading = ref(false)
const defaultImage = 'https://via.placeholder.com/300x200?text=No+Image'

const pagination = ref({
  page: 1,
  pageSize: 12,
  total: 0
})

// 获取分类列表
const fetchCategories = async () => {
  try {
    const res = await getCategoryList()
    if (res.code === 200) {
      categories.value = [{ id: null, name: '全部' }, ...res.data]
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取商品列表
const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.value.page,
      pageSize: pagination.value.pageSize,
      categoryId: selectedCategory.value,
      keyword: route.query.keyword
    }
    const res = await getProductList(params)
    if (res.code === 200) {
      productList.value = res.data.list || []
      pagination.value.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取商品列表失败')
    console.error('获取商品失败:', error)
  } finally {
    loading.value = false
  }
}

// 选择分类
const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  pagination.value.page = 1
  fetchProducts()
}

// 跳转详情
const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

// 分页变化
const handlePageChange = (page) => {
  pagination.value.page = page
  fetchProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  pagination.value.page = 1
  fetchProducts()
}

// 监听路由变化
watch(() => route.query.keyword, () => {
  pagination.value.page = 1
  fetchProducts()
})

onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.category-nav {
  background: #fff;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.category-scroll {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 5px;
}

.category-scroll::-webkit-scrollbar {
  height: 6px;
}

.category-scroll::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
}

.category-btn {
  white-space: nowrap;
}

.product-container {
  min-height: 400px;
}

.product-card {
  cursor: pointer;
  margin-bottom: 20px;
  transition: all 0.3s;
  border-radius: 8px;
  overflow: hidden;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.product-image {
  width: 100%;
  height: 200px;
}

.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: #f5f5f5;
  font-size: 40px;
  color: #ccc;
}

.product-info {
  padding: 12px;
}

.product-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  height: 2.8em;
}

.product-price {
  color: #ff4d4f;
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 8px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.location,
.views {
  display: flex;
  align-items: center;
  gap: 4px;
}

.loading-container {
  text-align: center;
  padding: 40px;
  font-size: 32px;
  color: #409eff;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .main-content {
    padding: 10px;
  }

  .product-image {
    height: 150px;
  }
}
</style>
