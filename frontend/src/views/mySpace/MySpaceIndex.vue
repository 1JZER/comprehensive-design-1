<template>
  <div style="display: flex; height: 100%">
    <!-- 左侧边栏   -->
    <div class="options-box">
      <div class="option-title flex-box">
        <div>
          历史记录<span> 共{{ editableTabs?.length }}个</span>
        </div>
      </div>


      <!-- 拖动选项 -->
      <ul class="sortOptions">
        <li v-for="(item, index) in historyList" :key="index">
          <div class="item-box flex-box hover-box" :class="{ selectedItem: selectedIndex === index }"
               @click="changeSelectIndex(index)">
            <div style="display: flex">
              <img src="@/assets/svg/移动竖.svg" width="13" style="margin-right: 3px" alt=""/>
              <span class="over-text">{{ item.topic }}</span>
            </div>
          </div>
        </li>
      </ul>


      <div class="recycle-bin">
        <!-- 当selectIndex等于-1时代表选中的是回收站 -->
        <div class="recycle-box hover-box" :class="{ selectedItem: selectedIndex === -1 }" @click="recycleBin">
          回收站
          <el-icon style="margin-left: 5px; font-size: 20px">
            <Delete/>
          </el-icon>
        </div>
      </div>
    </div>

    <!-- 主要数据展示区域 -->
    <div class="content-box">
      <div class="table-box">
        <!-- 默认展示创建短链输入框和按钮 -->
        <div class="buttons-box">
          <div style="width: 100%; display: flex">
            <input type="file" @change="handleFileUpload" id="file" style="display: none;"/>
            <el-button type="primary" @click="triggerFileInput">选择文件</el-button>
          </div>
        </div>

        <!-- 图片展示区域 -->
        <el-table :data="tableData" height="calc(100vh - 240px)" style="width: calc(100vw - 230px)"
                  :header-cell-style="{ background: '#f7f8fa', color: '#606266' }">
          <!-- 数据为空时展示的内容 -->
          <template #empty>
            <div style="height: 60vh; display: flex; align-items: center; justify-content: center">
              <img :src="imageUrl" alt="描述信息">
            </div>
          </template>
        </el-table>

      </div>
    </div>
  </div>
</template>

<script setup>
import {getCurrentInstance, onMounted, reactive, ref, watch} from 'vue'
import Sortable from 'sortablejs'
import {getLastWeekFormatDate, getTodayFormatDate} from '@/utils/plugins.js'
import {ElMessage} from 'element-plus'
import defaultImg from '@/assets/png/短链默认图标.png'

// 查看图表的时候传过去展示的，没什么用
const nums = ref(0)

const {proxy} = getCurrentInstance()
const API = proxy.$API
let selectedIndex = ref(0)
const editableTabs = ref([])
const historyList = ref([])


const statsFormData = reactive({
  endDate: getTodayFormatDate(),
  startDate: getLastWeekFormatDate(),
  size: 10,
  current: 1
})




// 将原来的数据转化为拖拽后传给后端的数据格式
const transformGroupData = (oldIndex, newIndex) => {
  // 相当于直接对展示数据进行修改
  const formData = editableTabs.value
  const tempData = formData.splice(oldIndex, 1)
  // console.log('调整的值', tempData)
  formData.splice(newIndex, 0, tempData[0])
  // console.log('这是经过转化的1', formData)
  formData.forEach((item, index) => {
    item.sortOrder = index
  })
  // console.log('这是经过转化的2', formData)
  return formData
}
// 拖拽
const initSortable = (className) => {
  const table = document.querySelector('.' + className)
  // console.log(table)
  // 创建拖拽实例
  Sortable.create(table, {
    animation: 150, //动画
    // 开始拖动事件
    onStart: () => {
      // console.log('开始拖动')
    },
    // 结束拖动事件
    onEnd: async ({to, from, oldIndex, newIndex, clone, pullMode}) => {
      // 当oldIndex不等于newIndex时才会去请求接口
      if (newIndex !== oldIndex) {
        // 对于不同情况下数据变化后的选中数据的实现
        if (selectedIndex.value === oldIndex) {
          selectedIndex.value = newIndex
        } else if (
            oldIndex < newIndex &&
            selectedIndex.value > oldIndex &&
            selectedIndex.value <= newIndex
        ) {
          selectedIndex.value = selectedIndex.value - 1
        } else if (
            oldIndex > newIndex &&
            selectedIndex.value < oldIndex &&
            selectedIndex.value >= newIndex
        ) {
          selectedIndex.value = selectedIndex.value + 1
        }
        const res = await API.group.sortGroup(transformGroupData(oldIndex, newIndex))
        // console.log('排序后的结果', res)
      }
    }
  })
}


// 改变选中分组时触发
watch(
    () => selectedIndex.value,
    (newValue) => {
      // -1为回收站，不需要重新请求正常页面数据
      if (newValue !== -1 && newValue !== -2) {
        queryPage()
      }
    }
)
onMounted(() => {
  initSortable('sortOptions')
})
const tableData = ref([])
const pageParams = reactive({
  gid: null,
  current: 1,
  size: 15,
  orderTag: null
})

watch(
    () => pageParams.orderTag,
    (nV) => {
      console.log(nV)
      queryPage()
    }
)
const totalNums = ref(0)


// 数据变化后更新当前页面
const queryPage = async () => {
  pageParams.gid = editableTabs.value?.[selectedIndex.value]?.gid
  nums.value = editableTabs.value?.[selectedIndex.value]?.shortLinkCount || 0
  console.log('------', editableTabs.value, selectedIndex.value)
  const res = await API.smallLinkPage.queryPage(pageParams)
  if (res?.data.success) {
    tableData.value = res.data?.data?.records
    totalNums.value = +res.data?.data?.total
  } else {
    ElMessage.error(res?.data.message)
  }
}


// 获取历史信息
const getHistoryInfo = async (fn) => {
  const res = await API.CVRequestHandler.listHistory()
  historyList.value = res.data?.data.historyList
  console.log("我的日志")
  console.log(historyList.value)

  fn && fn()
}


getHistoryInfo(queryPage)

// 获取分组信息，更新页面的分组模块
const getGroupInfo = async (fn) => {
  const res = await API.group.queryGroup()
  editableTabs.value = res.data?.data?.reverse()
  fn && fn()
}


// getGroupInfo(queryPage)

const updatePage = () => {
  getGroupInfo(queryPage)
}

// 是否展示回收站相关的组件
const isRecycleBin = ref(false)
const recycleBinNums = ref(0) // 回收站中的数量
// 获取回收站页面，gid到时候要删除
const queryRecycleBinPage = () => {
  API.smallLinkPage
      .queryRecycleBin({current: pageParams.current, size: pageParams.size})
      .then((res) => {
        tableData.value = res.data?.data?.records
        totalNums.value = +res.data?.data?.total
        recycleBinNums.value = totalNums.value
      })
}
// 点击回收站
const recycleBin = () => {
  isRecycleBin.value = true
  selectedIndex.value = -1 // -1作为回收站，-2作为选中其他
  pageParams.current = 1
  pageParams.size = 15
  // 点击回收站相关的请求
  queryRecycleBinPage()
}
// 点击分组中的选项
const changeSelectIndex = (index) => {
  selectedIndex.value = index
  isRecycleBin.value = false
  // 对应分组的数据请求
}
// 添加分组相关
const isAddGroup = ref(false)
const newGroupName = ref()
const addGroupLoading = ref(false)
// 展示添加分组弹框
const showAddGroup = () => {
  newGroupName.value = ''
  isAddGroup.value = true
}
const imageUrl = ref('');


// 处理图片上传
const triggerFileInput = () => {
  document.getElementById('file').click()
}
const handleFileUpload = async (event) => {
  const fileInput = event.target;
  const file = event.target.files[0];
  if (!file) return;
  try {
    const response = await API.CVRequestHandler.uploadFile(file);
    // 处理上传成功后的逻辑
    console.log("上传成功", response.data);
    imageUrl.value = URL.createObjectURL(response.data); // 直接使用 response.data，它是一个 Blob 对象

  } catch (error) {
    console.error("上传失败", error);
    // 处理上传失败后的逻辑
  } finally {
    // 清除 file input 以便再次上传
    fileInput.value = '';
  }
}



</script>

<style lang="scss" scoped>
.flex-box {
  display: flex;
  align-items: center;
  padding: 0 10px;
  justify-content: space-between;
}

.hover-box:hover {
  cursor: pointer;
  color: rgba(40, 145, 206, 0.6);
  background-color: #f7f7f7;
  box-shadow: 0px 2px 8px 0px rgba(28, 41, 90, 0.1);
}

.option-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 56px;
  font-size: 15px;
  font-weight: 600;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);

  span {
    font-size: 12px;
    font-weight: 400;
  }
}

.options-box {
  display: flex;
  flex-direction: column;
  position: relative;
  height: 100%;
  width: 190px;
  border-right: 1px solid rgba(0, 0, 0, 0.1);

  .item-box {
    height: 43px;
    width: 190px;
    font-family: PingFangSC-Semibold,
    PingFang SC;
    font-weight: 520;
  }

  .item-box:hover {
    .flex-box {
      .edit {
        display: block;
      }

      .item-length {
        display: none !important;
      }
    }
  }
}

.recycle-bin {
  position: absolute;
  display: flex;
  bottom: 0;
  height: 50px;
  width: 100%;
}

.recycle-box {
  flex: 1;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit {
  display: none;
  margin-left: 5px;
  color: rgb(83, 97, 97);
  font-size: 20px;
}

.edit:hover {
  color: #2991ce;
  cursor: pointer;
}

.zero {
  color: rgb(83, 97, 97) !important;
}

// 提示框样式
.tooltip-base-box {
  width: 600px;
}

.tooltip-base-box .row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.tooltip-base-box .center {
  justify-content: center;
}

.tooltip-base-box .box-item {
  width: 110px;
}

.selectedItem {
  color: #3464e0 !important;
  background-color: #ebeffa !important;
  font-weight: 600 !important;
}

.block:hover {
  color: rgb(121, 187, 255);

  .el-icon {
    color: rgb(121, 187, 255) !important;
  }
}

.table-edit {
  font-size: 20px;
  margin-right: 20px;
  color: #3677c2;
  cursor: pointer;
}

.table-edit:hover {
  color: #98cafe;
}

.qr-code {
  margin-right: 20px;
  cursor: pointer;
}

.qr-code:hover {
  opacity: 0.5;
}

.content-box {
  flex: 1;
  padding: 16px;
  background-color: #eef0f5;
  position: relative;

  .table-box {
    background-color: #ffffff;
    height: 100%;

    .buttons-box {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 16px;
    }

    .pagination-block {
      position: absolute;
      bottom: 4%;
      left: 50%;
      transform: translate(-50%, 0);
    }

    .recycle-bin-box {
      height: 64px;
      display: flex;
      align-items: center;
      padding-left: 16px;

      span:nth-child(1) {
        font-size: 20px;
        margin-right: 5px;
      }
    }
  }
}

.over-text {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box; //作为弹性伸缩盒子模型显示。
  -webkit-box-orient: vertical; //设置伸缩盒子的子元素排列方式--从上到下垂直排列
  -webkit-line-clamp: 1; //显示的行
}

.table-link-box {
  display: flex;
  align-items: center;

  .name-date {
    display: flex;
    flex-direction: column;
    margin-left: 10px;

    span:nth-child(1) {
      font-size: 15px;
      font-weight: 500;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box; //作为弹性伸缩盒子模型显示。
      -webkit-box-orient: vertical; //设置伸缩盒子的子元素排列方式--从上到下垂直排列
      -webkit-line-clamp: 1; //显示的行
    }

    .time {
      display: flex;
      align-items: center;

      span {
        font-size: 12px;
      }

      img {
        margin-left: 10px;
      }

      div {
        border: 1.5px solid rgb(253, 81, 85);
        border-radius: 8px;
        line-height: 20px;
        font-size: 12px;
        transform: scale(0.7);
        color: rgb(253, 81, 85);
        padding: 0 4px;
        background-color: rgba(250, 210, 211);

        span {
          font-weight: bolder;
        }
      }
    }
  }
}

.isExpire {
  .name-date {
    span:nth-child(1) {
      color: rgba(0, 0, 0, 0.3);
    }

    .time {
      div {
        span {
          font-weight: bolder;
          color: red;
        }
      }
    }
  }
}

.table-url-box {
  display: flex;
  flex-direction: column;
  align-items: flex-start;

  span {
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box; //作为弹性伸缩盒子模型显示。
    -webkit-box-orient: vertical; //设置伸缩盒子的子元素排列方式--从上到下垂直排列
    -webkit-line-clamp: 1; //显示的行
    color: rgba(0, 0, 0, 0.4);
  }
}

.times-box {
  display: flex;
  flex-direction: column;

  .today-box {
    span {
      font-size: 13px;
      font-weight: 600;
      margin-right: 10px;
    }

    span:nth-child(1) {
      font-weight: 400;
      color: rgba(0, 0, 0, 0.4);
    }
  }

  .total-box {
    span {
      font-size: 13px;
      font-weight: 400;
      margin-right: 10px;
    }

    span:nth-child(1) {
      font-weight: 400;
      color: rgba(0, 0, 0, 0.4);
    }
  }
}

.copy-url {
  margin-left: 10px;
}

.demo-tabs > .el-tabs__content {
  font-size: 32px;
  font-weight: 600;
}

.demo-tabs .custom-tabs-label .el-icon {
  vertical-align: middle;
}

.demo-tabs .custom-tabs-label span {
  vertical-align: middle;
  margin-left: 4px;
}

.orderIndex {
  color: #3677c2;
}

.sortOptions {
  height: calc(100% - 50px);
  margin-bottom: 50px;
  // height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
}
</style>
