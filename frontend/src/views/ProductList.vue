<template>
  <div class="product-list-page">
    <Header />

    <div class="main-content">
      <el-row :gutter="20">
        <!-- 左侧筛选栏 -->
        <el-col :xs="24" :sm="6" :md="5">
          <div class="filter-panel">
            <div class="filter-section">
              <h3 class="filter-title">分类</h3>
              <el-radio-group v-model="filters.categoryId" @change="handleFilterChange">
                <el-radio :label="null" class="filter-radio">全部</el-radio>
                <el-radio
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.id"
                  class="filter-radio"
                >
                  {{ category.name }}
                </el-radio>
              </el-radio-group>
            </div>

            <el-divider />

            <div class="filter-section">
              <h3 class="filter-title">价格区间</h3>
              <el-radio-group v-model="filters.priceRange" @change="handleFilterChange">
                <el-radio :label="null" class="filter-radio">不限</el-radio>
                <el-radio label="0-100" class="filter-radio">0-100元</el-radio>
                <el-radio label="100-500" class="filter-radio">100-500元</el-radio>
                <el-radio label="500-1000" class="filter-radio">500-1000元</el-radio>
                <el-radio label="1000-5000" class="filter-radio">1000-5000元</el-radio>
                <el-radio label="5000+" class="filter-radio">5000元以上</el-radio>
              </el-radio-group>
              <div class="custom-price">
                <el-input
                  v-model="customPrice.min"
                  placeholder="最低价"
                  type="number"
                  size="small"
                />
                <span>-</span>
                <el-input
                  v-model="customPrice.max"
                  placeholder="最高价"
                  type="number"
                  size="small"
                />
                <el-button size="small" type="primary" @click="applyCustomPrice">
                  确定
                </el-button>
              </div>
            </div>

            <el-divider />

            <div class="filter-section">
              <h3 class="filter-title">成色</h3>
              <el-radio-group v-model="filters.condition" @change="handleFilterChange">
                <el-radio :label="null" class="filter-radio">不限</el-radio>
                <el-radio label="全新" class="filter-radio">全新</el-radio>
                <el-radio label="99新" class="filter-radio">99新</el-radio>
                <el-radio label="95新" class="filter-radio">95新</el-radio>
                <el-radio label="9成新" class="filter-radio">9成新</el-radio>
                <el-radio label="8成新" class="filter-radio">8成新</el-radio>
              </el-radio-group>
            </div>

            <el-divider />

            <el-button type="danger" plain @click="resetFilters" style="width: 100%">
              重置筛选
            </el-button>
          </div>
        </el-col>

        <!-- 右侧商品列表 -->
        <el-col :xs="24" :sm="18" :md="19">
          <!-- 排序栏 -->
          <div class="sort-bar">
            <el-radio-group v-model="sortType" @change="handleSortChange">
              <el-radio-button label="default">综合</el-radio-button>
              <el-radio-button label="latest">最新</el-radio-button>
              <el-radio-button label="price_asc">价格从低到高</el-radio-button>
              <el-radio-button label="price_desc">价格从高到低</el-radio-button>
            </el-radio-group>
            <div class="result-count">共 {{ pagination.total }} 件商品</div>
          </div>

          <!-- 商品列表 -->
          <div class="product-list">
            <el-row :gutter="15">
              <el-col
                :xs="12"
                :sm="8"
                :md="6"
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
        </el-col>
      </el-row>
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

const categories = ref([])
const productList = ref([])
const loading = ref(false)
const defaultImage = 'https://via.placeholder.com/300x200?text=No+Image'

const filters = ref({
  categoryId: null,
  priceRange: null,
  condition: null,
  minPrice: null,
  maxPrice: null
})

const customPrice = ref({
  min: '',
  max: ''
})

const sortType = ref('default')

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
      categories.value = res.data || []
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
      categoryId: filters.value.categoryId,
      minPrice: filters.value.minPrice,
      maxPrice: filters.value.maxPrice,
      condition: filters.value.condition,
      sortType: sortType.value,
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

// 筛选变化
const handleFilterChange = () => {
  // 处理价格区间
  if (filters.value.priceRange) {
    if (filters.value.priceRange === '5000+') {
      filters.value.minPrice = 5000
      filters.value.maxPrice = null
    } else {
      const [min, max] = filters.value.priceRange.split('-')
      filters.value.minPrice = Number(min)
      filters.value.maxPrice = Number(max)
    }
  } else {
    filters.value.minPrice = null
    filters.value.maxPrice = null
  }

  pagination.value.page = 1
  fetchProducts()
}

// 应用自定义价格
const applyCustomPrice = () => {
  const min = Number(customPrice.value.min)
  const max = Number(customPrice.value.max)

  if (min && max && min > max) {
    ElMessage.warning('最低价不能大于最高价')
    return
  }

  filters.value.priceRange = null
  filters.value.minPrice = min || null
  filters.value.maxPrice = max || null
  pagination.value.page = 1
  fetchProducts()
}

// 排序变化
const handleSortChange = () => {
  pagination.value.page = 1
  fetchProducts()
}

// 重置筛选
const resetFilters = () => {
  filters.value = {
    categoryId: null,
    priceRange: null,
    condition: null,
    minPrice: null,
    maxPrice: null
  }
  customPrice.value = {
    min: '',
    max: ''
  }
  sortType.value = 'default'
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
.product-list-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.filter-panel {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 80px;
}

.filter-section {
  margin-bottom: 10px;
}

.filter-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

.filter-radio {
  display: block;
  margin-bottom: 10px;
}

.custom-price {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
}

.custom-price .el-input {
  flex: 1;
}

.sort-bar {
  background: #fff;
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.result-count {
  color: #999;
  font-size: 14px;
}

.product-list {
  min-height: 400px;
}

.product-card {
  cursor: pointer;
  margin-bottom: 15px;
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
  height: 180px;
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
  font-size: 18px;
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
  margin-top: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .main-content {
    padding: 10px;
  }

  .filter-panel {
    position: static;
    margin-bottom: 15px;
  }

  .sort-bar {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }

  .product-image {
    height: 150px;
  }
}
</style>
