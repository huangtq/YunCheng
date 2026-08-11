<template>
  <div class="app-container">
    <div class="grid-workbench">
      <div class="grid-editor">
        <div class="template-config-card mb8">
          <div class="template-config-form">
        <el-form :model="configForm" inline>
          <el-form-item label="会议模板">
            <el-select
              v-model="configForm.gridTemplate"
              filterable
              style="width: 330px"
              placeholder="请选择会议模板"
            >
              <el-option
                v-for="item in templateOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
                <el-popover
                  placement="right-start"
                  trigger="hover"
                  :width="240"
                  :show-after="120"
                  :hide-after="120"
                  popper-class="meeting-template-popover"
                >
                  <template #reference>
                    <div class="template-option">{{ item.label }}</div>
                  </template>
                  <div class="template-popover-content">
                    <div class="template-popover-title">{{ item.label }}</div>
                    <el-image
                      class="template-popover-image"
                      :src="item.preview"
                      :preview-src-list="[item.preview]"
                      fit="contain"
                      preview-teleported
                    />
                    <div v-if="item.description" class="template-popover-description">
                      {{ item.description }}
                    </div>
                  </div>
                </el-popover>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              @click="saveTemplate"
              v-hasPermi="['meeting:activity:edit']"
            >
              保存模板
            </el-button>
          </el-form-item>
          <el-form-item class="layout-settings-item">
            <el-popover placement="bottom-start" :width="384" trigger="click" popper-class="grid-visual-popper">
              <template #reference>
                <el-button plain icon="Operation">移动端布局</el-button>
              </template>
              <div class="grid-visual-panel">
                <div class="grid-visual-header">
                  <div>
                    <div class="grid-visual-title">移动端布局</div>
                    <div class="grid-visual-hint">调整后点击“保存模板”才会同步到报名页</div>
                  </div>
                  <span class="grid-visual-unit">单位：rpx</span>
                </div>
                <div class="grid-visual-section">
                  <span>主视觉</span>
                  <label>高度<el-input-number v-model="gridVisual.heroHeight" :min="0" :max="1600" :step="10" /></label>
                  <small>设为 0 时，主图按图片原始比例展示</small>
                </div>
                <div class="grid-visual-section grid-visual-section--two">
                  <span>倒计时</span>
                  <label>上边距<el-input-number v-model="gridVisual.countdownTop" :min="0" :max="200" :step="2" /></label>
                  <label>下边距<el-input-number v-model="gridVisual.countdownBottom" :min="0" :max="200" :step="2" /></label>
                </div>
                <div class="grid-visual-section grid-visual-section--two">
                  <span>九宫格</span>
                  <label>卡片间距<el-input-number v-model="gridVisual.itemGap" :min="0" :max="100" :step="2" /></label>
                  <label>外侧留白<el-input-number v-model="gridVisual.itemPadding" :min="0" :max="100" :step="2" /></label>
                </div>
              </div>
            </el-popover>
          </el-form-item>
        </el-form>
        <div class="template-tip">
          当前移动端实际生效的模板：列数（1/2/3）、纯图标/图文，以及「不规则 Tile 宫格」。保存模板后右侧预览会同步。
        </div>
          </div>
        </div>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:grid:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain @click="handleSyncModules" v-hasPermi="['meeting:grid:add']">同步常用模块</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:grid:remove']">删除</el-button>
          </el-col>
        </el-row>

        <el-table v-loading="loading" :data="gridList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="图标" align="center" width="90">
            <template #default="scope">
              <MeetingIcon
                v-if="scope.row.iconType === 'icon' && scope.row.iconKey"
                :icon-key="scope.row.iconKey"
                :size="40"
                color="#4f46e5"
              />
              <div v-else-if="scope.row.iconUrl" class="grid-icon-thumb">
                <el-image
                  :src="resolveUrl(scope.row.iconUrl)"
                  fit="contain"
                />
              </div>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="标题" align="center" prop="title" min-width="120" />
          <el-table-column label="链接类型" align="center" prop="linkType" width="100">
            <template #default="scope">
              <span>{{ linkTypeLabel(scope.row.linkType) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="模块/外链" align="center" min-width="160" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-if="scope.row.linkType === 'module'">{{ moduleLabel(scope.row.moduleKey) }}</span>
              <span v-else-if="scope.row.linkType === 'url'">{{ scope.row.externalUrl }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="排序" align="center" prop="sortOrder" width="80" />
          <el-table-column label="状态" align="center" prop="status" width="90">
            <template #default="scope">
              <el-tag :type="scope.row.status === '1' ? 'success' : 'info'">{{ scope.row.status === '1' ? '启用' : '停用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="220" fixed="right">
            <template #default="scope">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['meeting:grid:edit']">修改</el-button>
              <el-button
                v-if="scope.row.linkType === 'module' && getMeetingModule(scope.row.moduleKey)"
                link
                type="success"
                @click="goModuleConfig(scope.row.moduleKey)"
              >去配置</el-button>
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['meeting:grid:remove']">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total > 0"
          :total="total"
          :page="queryParams.pageNum"
          :limit="queryParams.pageSize"
          @update:page="queryParams.pageNum = $event"
          @update:limit="queryParams.pageSize = $event"
          @pagination="getList"
        />
      </div>

      <aside class="phone-preview-panel">
        <div class="phone-frame">
          <div class="phone-screen" :class="{ 'is-tile-screen': isTilePreview }" :style="previewGridVisualStyle">
            <div
              v-if="isTilePreview"
              class="phone-tile-page"
              :class="{ 'is-light-tile': isLightTilePreview }"
              :style="previewTilePageStyle"
            >
              <div class="phone-tile-cover" :style="previewTileCoverStyle"></div>
              <div v-if="String(configForm.showCountdown) === '1'" class="phone-tile-countdown">
                <div v-if="configForm.countdownStyle === 'digital'" class="phone-countdown-board">
                  <div class="phone-countdown-heading">距会议开始还有</div>
                  <div class="phone-countdown-groups">
                    <div v-for="item in previewCountdownParts" :key="item.label" class="phone-countdown-group">
                      <div class="phone-flip-pair">
                        <span class="phone-flip-card">{{ item.value[0] }}</span>
                        <span class="phone-flip-card">{{ item.value[1] }}</span>
                      </div>
                      <span>{{ item.label }}</span>
                    </div>
                  </div>
                </div>
                <span v-else class="phone-tile-countdown-text">{{ countdownPreviewText }}</span>
              </div>
              <div class="phone-tile-preview">
                <div
                  v-for="item in previewItems"
                  :key="item.gridId"
                  class="phone-tile-item"
                  :class="{
                    'is-color-tile': isColorTile(item),
                    'is-tall-color-tile': isTallColorTile(item)
                  }"
                  :style="previewTileStyle(item)"
                >
                  <span v-if="!item.iconUrl || isColorTile(item)" class="phone-tile-title">{{ item.title }}</span>
                  <img
                    v-if="isColorTile(item) && item.iconUrl"
                    class="phone-tile-icon"
                    :src="resolveUrl(item.iconUrl)"
                    alt=""
                  />
                </div>
                <div v-if="!previewItems.length" class="phone-empty">暂无启用的菜单项</div>
              </div>
              <div v-if="previewFooter.enabled" class="phone-tile-footer">
                <img
                  v-if="previewFooter.logoUrl"
                  class="phone-tile-footer-logo"
                  :src="resolveUrl(previewFooter.logoUrl)"
                  alt=""
                />
                <span v-if="previewFooter.text">{{ previewFooter.text }}</span>
                <span v-if="previewFooter.company" class="phone-tile-footer-name">{{ previewFooter.company }}</span>
              </div>
            </div>

            <template v-else>
              <div class="phone-cover" :class="{ 'is-image-card-cover': hasImageCardPreview }" :style="previewCoverStyle">
                <img
                  v-if="hasImageCardPreview && activityInfo.coverUrl"
                  class="phone-cover-auto-image"
                  :src="resolveUrl(activityInfo.coverUrl)"
                  alt=""
                />
                <div v-if="!hasImageCardPreview" class="phone-cover-title">{{ activityInfo.activityName || "会议名称" }}</div>
                <div v-if="String(configForm.showRegisterCount) === '1'" class="phone-cover-meta">
                  已报名 {{ activityInfo.registerCount || 0 }} 人
                </div>
                <div v-if="String(configForm.showCountdown) === '1' && !hasImageCardPreview" class="phone-cover-countdown">
                  <div v-if="configForm.countdownStyle === 'digital'" class="phone-countdown-board">
                    <div class="phone-countdown-heading">距会议开始还有</div>
                    <div class="phone-countdown-groups">
                      <div v-for="item in previewCountdownParts" :key="item.label" class="phone-countdown-group">
                        <div class="phone-flip-pair">
                          <span class="phone-flip-card">{{ item.value[0] }}</span>
                          <span class="phone-flip-card">{{ item.value[1] }}</span>
                        </div>
                        <span>{{ item.label }}</span>
                      </div>
                    </div>
                  </div>
                  <span v-else>{{ countdownPreviewText }}</span>
                </div>
              </div>
              <div v-if="String(configForm.showCountdown) === '1' && hasImageCardPreview" class="phone-image-card-countdown">
                <div v-if="configForm.countdownStyle === 'digital'" class="phone-countdown-board">
                  <div class="phone-countdown-heading">距会议开始还有</div>
                  <div class="phone-countdown-groups">
                    <div v-for="item in previewCountdownParts" :key="item.label" class="phone-countdown-group">
                      <div class="phone-flip-pair">
                        <span class="phone-flip-card">{{ item.value[0] }}</span>
                        <span class="phone-flip-card">{{ item.value[1] }}</span>
                      </div>
                      <span>{{ item.label }}</span>
                    </div>
                  </div>
                </div>
                <span v-else>{{ countdownPreviewText }}</span>
              </div>
              <div
                class="phone-grid"
                :class="[previewGridClass, previewGridStyleClass, { 'grid-image-cards': hasImageCardPreview }]"
              >
                <div
                  v-for="item in previewItems"
                  :key="item.gridId"
                  class="phone-grid-item"
                  :class="{ 'is-image-card': isImageCardPreview(item) }"
                >
                  <img
                    v-if="isImageCardPreview(item)"
                    class="phone-grid-card-image"
                    :src="resolveUrl(gridCardUrl(item))"
                    alt=""
                  />
                  <template v-else>
                    <div class="phone-grid-icon" :style="{ background: previewIconSurfaceColor }">
                      <MeetingIcon
                        v-if="item.iconType === 'icon' && item.iconKey"
                        :icon-key="item.iconKey"
                        :size="34"
                        color="#fff"
                      />
                      <img v-else-if="item.iconUrl" :src="resolveUrl(item.iconUrl)" alt="" />
                      <el-icon v-else :size="28"><Grid /></el-icon>
                    </div>
                    <span v-if="!isIconOnlyPreview">{{ item.title }}</span>
                  </template>
                </div>
                <div v-if="!previewItems.length" class="phone-empty">暂无启用的菜单项</div>
              </div>
            </template>
          </div>
        </div>
      </aside>
    </div>

    <el-dialog :title="title" v-model="open" width="820px" append-to-body destroy-on-close>
      <el-scrollbar max-height="70vh">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="grid-form">
          <section class="grid-form-section">
            <h3>基础信息</h3>
            <el-row :gutter="24">
              <el-col :span="14">
                <el-form-item label="标题" prop="title">
                  <el-input v-model="form.title" maxlength="100" placeholder="标题" />
                </el-form-item>
              </el-col>
              <el-col :span="10">
                <el-form-item label="排序" prop="sortOrder">
                  <el-input-number v-model="form.sortOrder" :min="0" :max="9999" controls-position="right" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="移动端图标">
              <el-radio-group v-model="form.iconType" @change="handleIconTypeChange">
                <el-radio-button value="image">图片素材</el-radio-button>
                <el-radio-button value="icon">图标库</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="form.iconType === 'image'" label="素材图片" prop="iconUrl">
              <material-select v-model="form.iconUrl" :show-tip="false" />
            </el-form-item>
            <el-form-item v-else label="图标选择" prop="iconKey">
              <meeting-icon-select v-model="form.iconKey" />
            </el-form-item>
            <el-form-item v-if="form.iconType === 'image'" label="两列展示">
              <el-switch v-model="form.displayAsCard" active-text="整图卡片" inactive-text="图标入口" />
            </el-form-item>
          </section>

          <section class="grid-form-section">
            <h3>点击后的动作</h3>
            <el-form-item label="动作类型" prop="linkType">
              <el-radio-group v-model="form.linkType" class="action-type-group">
                <el-radio-button value="none">无动作</el-radio-button>
                <el-radio-button value="module">模块跳转</el-radio-button>
                <el-radio-button value="url">外部链接</el-radio-button>
                <el-radio-button value="content">内容页</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="form.linkType === 'module'" label="模块跳转" prop="moduleKey">
              <el-select v-model="form.moduleKey" placeholder="请选择模块" style="width: 360px" @change="onModuleChange">
                <el-option
                  v-for="item in moduleOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <div v-if="selectedModule" class="module-tip">
                <span>{{ selectedModule.desc }}</span>
                <el-button link type="primary" @click="goModuleConfig(form.moduleKey)">去配置该模块数据</el-button>
              </div>
            </el-form-item>
            <el-form-item v-if="form.linkType === 'url'" label="外部链接" prop="externalUrl">
              <el-input v-model="form.externalUrl" maxlength="500" placeholder="https://" style="max-width: 620px" />
            </el-form-item>
            <el-form-item v-if="form.linkType === 'content'" label="内容">
              <el-select v-model="form.contentType" style="width: 180px; margin-bottom: 10px">
                <el-option label="文字内容" value="text" />
                <el-option label="长图内容" value="image" />
              </el-select>
              <material-select
                v-if="form.contentType === 'image'"
                v-model="form.contentUrl"
                :show-tip="false"
              />
              <editor v-else v-model="form.content" :min-height="220" />
            </el-form-item>
          </section>

          <section class="grid-form-section">
            <h3>展示设置</h3>
            <el-form-item label="隐藏">
              <el-switch v-model="form.hidden" active-text="隐藏此菜单" />
            </el-form-item>
            <template v-if="isTilePreview">
              <el-divider content-position="left">Tile 色块与布局</el-divider>
              <el-form-item label="色块背景">
                <div class="tile-bg-editor">
                  <div class="tile-bg-toolbar">
                    <el-color-picker
                      :model-value="solidColorValue"
                      show-alpha
                      @change="onSolidColorPick"
                    />
                    <span class="tile-bg-tip">点选写入纯色；渐变请直接改右侧 CSS</span>
                  </div>
                  <el-input
                    v-model="form.gradientColor"
                    type="textarea"
                    :rows="2"
                    placeholder="例：#e91e63 或 linear-gradient(to right, rgb(240, 98, 146), rgb(194, 24, 91))"
                    @change="onGradientColorChange"
                  />
                  <div
                    v-if="form.gradientColor || form.tileBg"
                    class="tile-bg-swatch"
                    :style="tileBgPreviewStyle"
                  />
                </div>
              </el-form-item>
              <el-row :gutter="24">
                <el-col :span="6">
                  <el-form-item label="行">
                    <el-input-number v-model="form.tileRow" :min="0" :max="20" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="列">
                    <el-input-number v-model="form.tileCol" :min="0" :max="6" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="跨行">
                    <el-input-number v-model="form.tileRowSpan" :min="1" :max="6" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="跨列">
                    <el-input-number v-model="form.tileColSpan" :min="1" :max="6" />
                  </el-form-item>
                </el-col>
              </el-row>
            </template>
          </section>
        </el-form>
      </el-scrollbar>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MeetingGrid">
import { listGrid, getGrid, addGrid, updateGrid, delGrid } from "@/api/meeting/grid"
import { getActivity } from "@/api/meeting/activity"
import { getActivityConfig, updateActivityConfig } from "@/api/meeting/config"
import MaterialSelect from "@/components/MaterialSelect"
import MeetingIcon from "@/components/MeetingIcon"
import MeetingIconSelect from "@/components/MeetingIconSelect"
import { MEETING_MODULE_OPTIONS, getMeetingModule, meetingModuleLabel } from "@/utils/meetingModules"

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()
const baseUrl = import.meta.env.VITE_APP_BASE_API

const activityId = computed(() => route.query.id)
const activityInfo = ref({})
const loading = ref(true)
const gridList = ref([])
const total = ref(0)
const ids = ref([])
const multiple = ref(true)
const open = ref(false)
const title = ref("")
const configForm = ref({ gridTemplate: "1" })
const gridVisual = reactive({
  heroHeight: 0,
  countdownTop: 16,
  countdownBottom: 20,
  itemGap: 10,
  itemPadding: 10
})

const templateOptions = [
  {
    value: "1",
    label: "三列图文宫格",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20231225/3181354ccb4b44f0984581f5401a18fc.jpg",
    description: "每行 3 个入口，图标 + 标题"
  },
  {
    value: "5",
    label: "三列纯图标",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20231225/00ed86c2392449e28046363a8327bbb4.jpg",
    description: "每行 3 个入口，仅展示图标"
  },
  {
    value: "68",
    label: "两列图文宫格",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20260428/902a51e381f7464abb1441a5f6fc220a.png",
    description: "每行 2 个入口，图标 + 标题"
  },
  {
    value: "7",
    label: "一列图文列表",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20240528/f372efc58bfe4b93900b0dd93c113f6e.jpg",
    description: "单列列表，图标 + 标题"
  },
  {
    value: "71",
    label: "一列纯图标",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20240528/f372efc58bfe4b93900b0dd93c113f6e.jpg",
    description: "单列列表，仅展示图标"
  },
  {
    value: "tile",
    label: "不规则 Tile 宫格",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20260428/902a51e381f7464abb1441a5f6fc220a.png",
    description: "支持色块背景、行列位置和跨行跨列，适合活动首页定制布局"
  }
]

const moduleOptions = MEETING_MODULE_OPTIONS
function moduleLabel(v) {
  return meetingModuleLabel(v)
}

const previewItems = computed(() => {
  return gridList.value
    .filter(item => String(item.status) !== "0")
    .sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
})

const themeColor = computed(() => configForm.value.mobileThemeColor || "#1f6feb")
const previewIconSurfaceColor = computed(() => (
  isLightColor(themeColor.value) ? "#4f46e5" : themeColor.value
))

const previewGridClass = computed(() => {
  const template = String(configForm.value.gridTemplate)
  if (["7", "71"].includes(template)) return "grid-one"
  if (["68", "681"].includes(template)) return "grid-two"
  return "grid-three"
})

const isIconOnlyPreview = computed(() => ["5", "71"].includes(String(configForm.value.gridTemplate)))
const previewGridStyleClass = computed(() => (isIconOnlyPreview.value ? "grid-icon-only" : ""))
const hasImageCardPreview = computed(() => previewItems.value.some(item => isImageCardPreview(item)))
const isTilePreview = computed(() => String(configForm.value.gridTemplate) === "tile")
const isLightTilePreview = computed(() => isTilePreview.value && isLightColor(themeColor.value))
const tileSurfaceColor = computed(() => (isLightTilePreview.value ? (themeColor.value || "#f6f6f6") : "#061a74"))

function isImageCardPreview(item) {
  return item && item.iconType === "image" && !!gridCardUrl(item) && previewGridClass.value === "grid-two"
}

function gridCardUrl(item) {
  return item?.contentUrl || item?.iconUrl || ""
}
const solidColorValue = computed(() => {
  const value = String(form.value.gradientColor || form.value.tileBg || "").trim()
  if (!value || /gradient|url\(/i.test(value)) return ""
  return value
})
const tileBgPreviewStyle = computed(() => {
  const value = String(form.value.gradientColor || form.value.tileBg || "").trim()
  if (!value) return {}
  if (/gradient|url\(/i.test(value)) {
    return { backgroundImage: value }
  }
  return { backgroundColor: value }
})

const previewFooter = computed(() => ({
  enabled: String(configForm.value.footerEnabled) === "1",
  text: configForm.value.footerText || "",
  company: configForm.value.footerCompany || "",
  logoUrl: configForm.value.footerLogoUrl || "",
  linkUrl: configForm.value.footerLinkUrl || ""
}))

const previewTilePageStyle = computed(() => {
  const background = configForm.value.mobileBackgroundUrl
  return {
    backgroundColor: isLightTilePreview.value ? tileSurfaceColor.value : "#1100ab",
    backgroundImage: background ? `url("${resolveUrl(background)}")` : "none",
    backgroundSize: "100% 100%",
    backgroundPosition: "50% 100%",
    backgroundRepeat: "no-repeat"
  }
})

const previewTileCoverStyle = computed(() => {
  const coverUrl = resolveUrl(activityInfo.value.coverUrl)
  if (!coverUrl) {
    return {
      background: `linear-gradient(135deg, ${themeColor.value}, #0b3d91)`
    }
  }
  return {
    backgroundImage: `url("${coverUrl}")`,
    backgroundSize: "contain",
    backgroundPosition: "top center",
    backgroundRepeat: "no-repeat",
    backgroundColor: tileSurfaceColor.value
  }
})

function isLightColor(color) {
  if (!color || typeof color !== "string") return false
  const value = color.trim().toLowerCase()
  let r = 0
  let g = 0
  let b = 0
  if (value.startsWith("#")) {
    const hex = value.slice(1)
    const full = hex.length === 3
      ? hex.split("").map(ch => ch + ch).join("")
      : hex
    if (full.length < 6) return false
    r = parseInt(full.slice(0, 2), 16)
    g = parseInt(full.slice(2, 4), 16)
    b = parseInt(full.slice(4, 6), 16)
  } else {
    const match = value.match(/rgba?\((\d+)[,\s]+(\d+)[,\s]+(\d+)/)
    if (!match) return false
    r = Number(match[1])
    g = Number(match[2])
    b = Number(match[3])
  }
  if ([r, g, b].some(n => Number.isNaN(n))) return false
  const luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255
  return luminance >= 0.72
}

function parseTileMeta(item) {
  const raw = (item && item.remark) || ""
  if (!raw) return {}
  try {
    const parsed = JSON.parse(raw)
    if (!parsed || typeof parsed !== "object") return {}
    const bg = resolveTileBg(parsed)
    return {
      ...parsed,
      bg: bg || undefined
    }
  } catch {
    if (raw.startsWith("tile-bg:")) {
      return { bg: raw.slice(8) }
    }
    return {}
  }
}

function isColorTile(item) {
  const meta = parseTileMeta(item)
  return !!(meta.bg || meta.background || meta.tileBg || meta.gradientColor)
}

function isTallColorTile(item) {
  return isColorTile(item) && Number(item.tileRowSpan || 1) >= 2
}

function previewTileStyle(item) {
  const meta = parseTileMeta(item)
  const style = {
    gridColumn: `${item.tileCol || "auto"} / span ${item.tileColSpan || 1}`,
    gridRow: `${item.tileRow || "auto"} / span ${item.tileRowSpan || 1}`
  }
  const gradient = meta.bg || meta.background || meta.tileBg || meta.gradientColor
  if (gradient) {
    style.backgroundImage = gradient
    style.backgroundColor = "transparent"
  } else if (item.iconUrl) {
    style.backgroundImage = `url("${resolveUrl(item.iconUrl)}")`
  }
  return style
}

const countdownPreviewText = computed(() => {
  const style = configForm.value.countdownStyle || "classic"
  if (style === "digital") return "倒计时 · 数字翻牌样式"
  if (style === "simple") return "倒计时 · 简洁样式"
  return "倒计时 · 经典样式"
})

const previewCountdownParts = [
  { label: "Days", value: "00" },
  { label: "Hours", value: "00" },
  { label: "Minutes", value: "00" },
  { label: "Seconds", value: "00" }
]

const previewGridVisualStyle = computed(() => ({
  '--grid-hero-height': gridVisual.heroHeight > 0 ? `${gridVisual.heroHeight / 2}px` : 'auto',
  '--grid-countdown-top': `${gridVisual.countdownTop / 2}px`,
  '--grid-countdown-bottom': `${gridVisual.countdownBottom / 2}px`,
  '--grid-item-gap': `${gridVisual.itemGap / 2}px`,
  '--grid-item-padding': `${gridVisual.itemPadding / 2}px`
}))

const previewCoverStyle = computed(() => {
  const coverUrl = resolveUrl(activityInfo.value.coverUrl)
  if (coverUrl) {
    return {
      backgroundImage: `linear-gradient(180deg, rgba(0, 0, 0, 0.05), rgba(0, 0, 0, 0.55)), url("${coverUrl}")`
    }
  }
  return {
    backgroundImage: `linear-gradient(135deg, ${themeColor.value}, #0b3d91)`
  }
})

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  activityId: undefined
})

const form = ref({})
const selectedModule = computed(() => getMeetingModule(form.value.moduleKey))
const rules = {
  title: [{ required: true, message: "标题不能为空", trigger: "blur" }]
}

function resolveUrl(url) {
  if (!url) return ""
  if (url.startsWith("http://") || url.startsWith("https://")) return url
  return baseUrl + url
}

function linkTypeLabel(type) {
  if (type === "module") return "内置模块"
  if (type === "url") return "外部链接"
  if (type === "content") return "内容页"
  return "无"
}

function getList() {
  loading.value = true
  queryParams.value.activityId = activityId.value
  listGrid(queryParams.value).then(res => {
    gridList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}

function loadMeta() {
  getActivity(activityId.value).then(res => {
    activityInfo.value = res.data || {}
  })
  getActivityConfig(activityId.value).then(res => {
    configForm.value = {
      ...configForm.value,
      ...(res.data || {}),
      gridTemplate: normalizeTemplate(res.data?.gridTemplate)
    }
    Object.assign(gridVisual, parseGridVisual(res.data?.remark))
  })
}

function defaultGridVisual() {
  return { heroHeight: 0, countdownTop: 16, countdownBottom: 20, itemGap: 10, itemPadding: 10 }
}

function parseGridVisual(value) {
  const defaults = defaultGridVisual()
  try {
    const parsed = JSON.parse(value || "{}")
    return { ...defaults, ...(parsed.gridVisual || {}) }
  } catch {
    return defaults
  }
}

function buildConfigRemark() {
  let parsed = {}
  try {
    parsed = JSON.parse(configForm.value.remark || "{}")
  } catch {}
  return JSON.stringify({ ...parsed, gridVisual: { ...gridVisual } })
}

function normalizeTemplate(value) {
  const legacyMap = {
    grid3x3: "1",
    grid2x2: "5",
    list: "7",
    "62": "1",
    "63": "1",
    "64": "1",
    "65": "1",
    "651": "1",
    "681": "68"
  }
  const normalized = legacyMap[value] || String(value || "")
  return templateOptions.some(item => item.value === normalized) ? normalized : "1"
}

function saveTemplate() {
  updateActivityConfig({
    activityId: Number(activityId.value),
    gridTemplate: configForm.value.gridTemplate,
    remark: buildConfigRemark()
  }).then(() => proxy.$modal.msgSuccess("模板已保存"))
}

function handleSelectionChange(selection) {
  ids.value = selection.map(i => i.gridId)
  multiple.value = !selection.length
}

function parseGridOptions(remark) {
  if (!remark) return {}
  try {
    const options = JSON.parse(remark)
    if (!options || typeof options !== "object") return {}
    const tileBg = resolveTileBg(options)
    if (options.__gridForm) {
      return {
        ...options,
        gradientColor: options.gradientColor || tileBg || "",
        tileBg
      }
    }
    return {
      gradientColor: tileBg || "",
      tileBg
    }
  } catch {
    if (String(remark).startsWith("tile-bg:")) {
      const tileBg = String(remark).slice(8)
      return { gradientColor: tileBg, tileBg }
    }
    return {}
  }
}

function resolveTileBg(options = {}, depth = 0) {
  if (!options || typeof options !== "object" || depth > 3) return ""
  const direct = options.bg || options.background || options.tileBg || options.gradientColor || ""
  if (direct) return direct
  if (typeof options.remark !== "string" || !options.remark) return ""
  try {
    const nested = JSON.parse(options.remark)
    if (nested && typeof nested === "object") {
      return resolveTileBg(nested, depth + 1)
    }
  } catch {
    if (options.remark.startsWith("tile-bg:")) {
      return options.remark.slice(8)
    }
  }
  return ""
}

function restoreForm(data) {
  const options = parseGridOptions(data.remark)
  const tileBg = options.tileBg || ""
  return {
    ...data,
    iconType: data.iconType || (data.iconKey ? "icon" : "image"),
    iconKey: data.iconKey || "",
    content: data.content || options.content || "",
    contentType: data.contentType || "text",
    contentUrl: data.contentUrl || "",
    tileRow: data.tileRow || 0,
    tileCol: data.tileCol || 0,
    tileRowSpan: data.tileRowSpan || 1,
    tileColSpan: data.tileColSpan || 1,
    gradientColor: options.gradientColor || tileBg || "",
    tileBg,
    hidden: options.hidden !== undefined
      ? options.hidden === true || options.hidden === 1 || options.hidden === "1"
      : String(data.status) === "0",
    displayAsCard: options.displayAsCard === true || options.displayAsCard === 1 || options.displayAsCard === "1"
  }
}

function buildPayload() {
  const payload = { ...form.value }
  delete payload.parentId
  delete payload.animation
  delete payload.gradientColor
  delete payload.tileBg
  delete payload.opacity
  delete payload.hidden
  payload.status = form.value.hidden ? "0" : "1"
  payload.iconType = form.value.iconType || "image"
  payload.iconUrl = payload.iconType === "image" ? (form.value.iconUrl || "") : ""
  payload.iconKey = payload.iconType === "icon" ? (form.value.iconKey || "") : ""
  payload.contentType = form.value.contentType || "text"
  payload.contentUrl = payload.contentType === "image" ? (form.value.contentUrl || "") : ""
  const bg = String(form.value.gradientColor || form.value.tileBg || "").trim()
  const remarkObj = { __gridForm: true }
  if (form.value.displayAsCard) remarkObj.displayAsCard = true
  if (bg) {
    remarkObj.bg = bg
    remarkObj.gradientColor = bg
  }
  payload.remark = JSON.stringify(remarkObj)
  return payload
}

function onGradientColorChange(value) {
  const next = String(value ?? form.value.gradientColor ?? "").trim()
  form.value.gradientColor = next
  form.value.tileBg = next
}

function onSolidColorPick(value) {
  const next = value || ""
  form.value.gradientColor = next
  form.value.tileBg = next
}

function reset() {
  form.value = {
    gridId: undefined,
    activityId: Number(activityId.value),
    title: undefined,
    iconType: "image",
    iconKey: "",
    iconUrl: undefined,
    linkType: "content",
    moduleKey: "none",
    externalUrl: undefined,
    content: "",
    contentType: "text",
    contentUrl: "",
    tileRow: 0,
    tileCol: 0,
    tileRowSpan: 1,
    tileColSpan: 1,
    gradientColor: "",
    tileBg: "",
    displayAsCard: false,
    hidden: false,
    sortOrder: 0,
    status: "1"
  }
  proxy.resetForm("formRef")
}

function handleIconTypeChange(type) {
  if (type === "icon") {
    form.value.iconUrl = ""
    form.value.iconKey = form.value.iconKey || ""
  } else {
    form.value.iconKey = ""
  }
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "新增九宫格项"
}

function onModuleChange(key) {
  const mod = getMeetingModule(key)
  if (!mod) return
  if (!form.value.title) form.value.title = mod.label
  if (form.value.iconType === "icon" && !form.value.iconKey) {
    form.value.iconKey = mod.iconKey
  }
}

function goModuleConfig(key) {
  const mod = getMeetingModule(key)
  if (!mod?.adminPath) {
    proxy.$modal.msgWarning("该模块暂无后台配置页")
    return
  }
  router.push({ path: mod.adminPath, query: { id: activityId.value } })
}

async function handleSyncModules() {
  const used = new Set(
    gridList.value
      .filter(i => i.linkType === "module" && i.moduleKey)
      .map(i => i.moduleKey)
  )
  const missing = MEETING_MODULE_OPTIONS
    .map(o => getMeetingModule(o.value))
    .filter(m => m && !used.has(m.key))
  if (!missing.length) {
    proxy.$modal.msgSuccess("常用模块均已在九宫格中")
    return
  }
  try {
    await proxy.$modal.confirm(`将补充 ${missing.length} 个常用模块入口（已有的不会重复）：${missing.map(m => m.label).join("、")}`)
  } catch {
    return
  }
  let sortBase = Math.max(0, ...gridList.value.map(i => i.sortOrder || 0))
  for (const mod of missing) {
    sortBase += 1
    await addGrid({
      activityId: Number(activityId.value),
      title: mod.label,
      iconType: "icon",
      iconKey: mod.iconKey,
      iconUrl: "",
      linkType: "module",
      moduleKey: mod.key,
      externalUrl: "",
      content: "",
      sortOrder: sortBase,
      status: "1",
      remark: JSON.stringify({ __gridForm: true })
    })
  }
  proxy.$modal.msgSuccess(`已同步 ${missing.length} 个模块`)
  getList()
}

function handleUpdate(row) {
  reset()
  getGrid(row.gridId).then(res => {
    form.value = restoreForm(res.data)
    open.value = true
    title.value = "修改九宫格项"
  })
}

function submitForm() {
  proxy.$refs["formRef"].validate(valid => {
    if (!valid) return
    const payload = buildPayload()
    const req = form.value.gridId ? updateGrid(payload) : addGrid(payload)
    req.then(() => {
      proxy.$modal.msgSuccess("操作成功")
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const gridIds = row?.gridId || ids.value
  proxy.$modal.confirm("是否确认删除选中的九宫格项？").then(() => delGrid(gridIds)).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

onMounted(() => {
  if (!activityId.value) {
    proxy.$modal.msgError("缺少会议ID")
    return
  }
  loadMeta()
  getList()
})
</script>

<style scoped>
.grid-workbench {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}
.grid-editor {
  flex: 1;
  min-width: 0;
}
.grid-icon-thumb {
  width: 40px;
  height: 40px;
  margin: 0 auto;
  overflow: hidden;
  border-radius: 6px;
  background-color: #3a3f4b;
  background-image:
    linear-gradient(45deg, #2f3440 25%, transparent 25%),
    linear-gradient(-45deg, #2f3440 25%, transparent 25%),
    linear-gradient(45deg, transparent 75%, #2f3440 75%),
    linear-gradient(-45deg, transparent 75%, #2f3440 75%);
  background-size: 8px 8px;
  background-position: 0 0, 0 4px, 4px -4px, -4px 0;
}
.grid-icon-thumb :deep(.el-image) {
  width: 100%;
  height: 100%;
}
.template-config-card {
  padding: 20px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
.template-config-form {
  width: 100%;
}
.template-tip {
  margin-top: 4px;
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
}
.template-option {
  width: 100%;
}
.template-popover-content {
  text-align: center;
}
.template-popover-title {
  margin-bottom: 8px;
  color: #303133;
  font-size: 13px;
  font-weight: 600;
}
.template-popover-image {
  width: 210px;
  height: 300px;
  overflow: hidden;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background: #f5f7fa;
}
.template-popover-description {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
  line-height: 1.5;
  text-align: left;
}
.grid-form {
  padding: 0 2px;
}
.grid-form-section {
  margin-bottom: 12px;
  padding: 16px 18px 4px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.03);
}
.grid-form-section:last-child {
  margin-bottom: 0;
}
.grid-form-section h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 14px;
  color: #303133;
  font-size: 15px;
  font-weight: 600;
}
.grid-form-section h3::before {
  width: 4px;
  height: 18px;
  border-radius: 2px;
  background: #409eff;
  content: "";
}
.grid-form-section :deep(.el-form-item:last-child) {
  margin-bottom: 0;
}
.grid-form-section :deep(.el-form-item__label) {
  color: #606266;
  padding-right: 12px;
  line-height: 32px;
}
.grid-form-section :deep(.el-form-item) {
  margin-bottom: 14px;
}
.grid-form-section :deep(.el-input),
.grid-form-section :deep(.el-select),
.grid-form-section :deep(.el-input-number) {
  max-width: 100%;
}
.grid-form-section :deep(.el-input-number) {
  width: 130px;
}
:deep(.el-dialog__body) {
  padding: 14px 18px 2px;
  background: #f5f7fa;
}
:deep(.el-dialog__header) {
  margin-right: 0;
  padding: 16px 20px 13px;
  border-bottom: 1px solid #ebeef5;
}
:deep(.el-dialog__title) {
  color: #1f2937;
  font-size: 16px;
  font-weight: 600;
}
:deep(.el-dialog__footer) {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 20px 14px;
  border-top: 1px solid #ebeef5;
}
:deep(.el-dialog__footer .el-button + .el-button) {
  margin-left: 0;
}
.grid-form-section :deep(.material-preview) {
  align-items: center;
  gap: 10px;
}
.grid-form-section :deep(.material-image-wrap.is-selected) {
  width: 96px;
  height: 96px;
  border-radius: 6px;
}
.grid-form-section :deep(.material-actions) {
  gap: 2px;
}
.grid-form-section :deep(.material-actions .el-button) {
  justify-content: flex-start;
  padding: 2px 4px;
}
.action-type-group {
  display: flex;
  flex-wrap: wrap;
}
.action-type-group :deep(.el-radio-button__inner) {
  min-width: 72px;
  padding: 8px 12px;
}
.module-tip {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
  color: #909399;
  font-size: 13px;
}
.tile-bg-editor {
  width: min(100%, 620px);
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.tile-bg-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
}
.tile-bg-tip {
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
}
.tile-bg-swatch {
  width: 100%;
  height: 44px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.2);
}
.grid-form-section :deep(.editor) {
  width: min(100%, 700px);
}
.grid-form-section :deep(.el-divider) {
  margin: 4px 0 16px;
}
.tile-bg-editor {
  width: min(100%, 560px);
}
.tile-bg-toolbar {
  flex-wrap: wrap;
}
@media (max-width: 700px) {
  :deep(.el-dialog) {
    width: calc(100vw - 24px) !important;
  }
  .grid-form-section :deep(.el-form-item__label) {
    float: none;
    display: block;
    width: auto !important;
    padding: 0;
    line-height: 24px;
    text-align: left;
  }
  .grid-form-section :deep(.el-form-item__content) {
    margin-left: 0 !important;
  }
  .action-type-group :deep(.el-radio-button__inner) {
    min-width: 0;
    padding: 8px 10px;
  }
}
.phone-preview-panel {
  flex: 0 0 360px;
  padding: 14px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
.phone-frame {
  width: 332px;
  margin: 0 auto;
  overflow: hidden;
  border: 2px solid #606266;
  border-radius: 3px;
  background: #f5f7fa;
}
.phone-screen {
  height: 720px;
  overflow-y: auto;
  background: #f5f7fa;
  scrollbar-width: thin;
}
.phone-screen.is-tile-screen {
  background: transparent;
}
.template-config-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.template-tip {
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
  padding: 0 2px 4px;
}
.layout-settings-item :deep(.el-button) {
  color: #526174;
  border-color: #d9e1ea;
  background: #fff;
}
.layout-settings-item :deep(.el-button:hover) {
  color: #1f6feb;
  border-color: #9fc5ee;
  background: #f3f8ff;
}
.grid-visual-panel {
  padding: 2px 0;
}
.grid-visual-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 0 2px 12px;
  border-bottom: 1px solid #edf0f4;
}
.grid-visual-title {
  color: #1f2937;
  font-size: 14px;
  font-weight: 600;
}
.grid-visual-hint {
  margin-top: 4px;
  color: #7a8493;
  font-size: 12px;
  line-height: 1.45;
}
.grid-visual-unit {
  flex: 0 0 auto;
  color: #8a94a3;
  font-size: 12px;
  line-height: 20px;
}
.grid-visual-section {
  display: grid;
  grid-template-columns: 62px minmax(0, 1fr);
  align-items: center;
  column-gap: 12px;
  row-gap: 8px;
  padding: 14px 2px;
  border-bottom: 1px solid #f0f2f5;
}
.grid-visual-section:last-child {
  border-bottom: 0;
  padding-bottom: 2px;
}
.grid-visual-section > span {
  align-self: start;
  padding-top: 7px;
  color: #526174;
  font-size: 13px;
  font-weight: 600;
}
.grid-visual-section label {
  display: grid;
  grid-template-columns: 52px minmax(0, 1fr);
  align-items: center;
  gap: 8px;
  color: #667085;
  font-size: 12px;
}
.grid-visual-section--two {
  grid-template-columns: 62px repeat(2, minmax(0, 1fr));
}
.grid-visual-section--two label {
  grid-template-columns: auto minmax(0, 1fr);
}
.grid-visual-section small {
  grid-column: 2;
  color: #98a2b3;
  font-size: 11px;
  line-height: 1.4;
}
.grid-visual-section :deep(.el-input-number) {
  width: 100%;
}
.phone-cover {
  display: flex;
  flex-direction: column;
  min-height: 150px;
  justify-content: flex-end;
  gap: 6px;
  padding: 18px;
  background: linear-gradient(135deg, #1d4ed8, #0ea5e9);
  background-position: center;
  background-size: cover;
}
.phone-cover.is-image-card-cover {
  min-height: var(--grid-hero-height, 0);
  height: var(--grid-hero-height, auto);
  padding: 0;
  background: none;
}
.phone-cover-auto-image {
  display: block;
  width: 100%;
  height: auto;
}
.phone-cover-title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.35;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.35);
}
.phone-cover-meta,
.phone-cover-countdown {
  color: rgba(255, 255, 255, 0.92);
  font-size: 12px;
  line-height: 1.4;
}
.phone-countdown-board {
  width: 100%;
  padding: 12px 0 9px;
  text-align: center;
}
.phone-countdown-heading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 10px;
  color: #fff;
  font-size: 13px;
  font-weight: 500;
}
.phone-countdown-heading::before,
.phone-countdown-heading::after {
  width: 26px;
  height: 1px;
  background: currentColor;
  content: "";
}
.phone-countdown-groups {
  display: flex;
  justify-content: center;
  gap: 7px;
}
.phone-countdown-group {
  color: #fff;
  font-size: 10px;
  text-align: center;
}
.phone-flip-pair {
  display: flex;
  gap: 2px;
}
.phone-flip-card {
  display: inline-flex;
  width: 24px;
  height: 32px;
  align-items: center;
  justify-content: center;
  border-radius: 3px;
  color: #687078;
  background: linear-gradient(180deg, #fff 0%, #e8ebee 48%, #bfc4c9 50%, #f8f9fa 52%, #d8dce0 100%);
  font-family: Georgia, "Times New Roman", serif;
  font-size: 16px;
}
.phone-grid {
  display: grid;
  gap: 1px;
  background: #e5e7eb;
}
.phone-grid.grid-one {
  grid-template-columns: 1fr;
}
.phone-grid.grid-two {
  grid-template-columns: repeat(2, 1fr);
}
.phone-grid.grid-three {
  grid-template-columns: repeat(3, 1fr);
}
.phone-image-card-countdown {
  padding: var(--grid-countdown-top, 8px) 10px var(--grid-countdown-bottom, 10px);
  color: #202a38;
  background: #eaf8ff;
  font-size: 12px;
  line-height: 1.4;
}
.phone-image-card-countdown .phone-countdown-heading {
  color: #202a38;
}
.phone-image-card-countdown .phone-countdown-heading::before,
.phone-image-card-countdown .phone-countdown-heading::after {
  background: rgba(32, 42, 56, 0.68);
}
.phone-image-card-countdown .phone-countdown-group {
  color: #202a38;
}
.phone-grid.grid-image-cards {
  gap: var(--grid-item-gap, 10px);
  padding: var(--grid-item-padding, 12px);
  background: #eaf8ff;
}
.phone-grid-item {
  display: flex;
  min-height: 100px;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 8px;
  padding: 12px 6px;
  color: #303133;
  background: #fff;
  font-size: 13px;
  text-align: center;
}
.phone-grid.grid-one .phone-grid-item {
  flex-direction: row;
  justify-content: flex-start;
  gap: 12px;
  padding: 14px 16px;
  min-height: 72px;
}
.phone-grid-item.is-image-card {
  display: block;
  min-height: 0;
  padding: var(--grid-item-padding, 0);
  background: transparent;
}
.phone-grid-card-image {
  display: block;
  width: 100%;
  border-radius: 5px;
  box-shadow: none;
}
.phone-grid-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
.phone-grid.grid-icon-only .phone-grid-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
}
.phone-grid-item:not(.is-image-card) img {
  width: 28px;
  height: 28px;
  object-fit: contain;
}
.phone-grid-item .el-icon {
  color: inherit;
}
.phone-tile-page {
  min-height: 100%;
  background: #061a74;
}
.phone-tile-page.is-light-tile {
  background: #f6f6f6;
}
.phone-tile-cover {
  width: 100%;
  aspect-ratio: 16 / 9;
  background-color: #061a74;
  background-size: contain;
  background-position: top center;
  background-repeat: no-repeat;
}
.phone-tile-page.is-light-tile .phone-tile-cover {
  background-color: #f6f6f6;
}
.phone-tile-countdown {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px 8px;
  background: #061a74;
}
.phone-tile-page.is-light-tile .phone-tile-countdown {
  background: #f6f6f6;
}
.phone-tile-page.is-light-tile .phone-countdown-heading,
.phone-tile-page.is-light-tile .phone-countdown-group,
.phone-tile-countdown-text {
  color: #303133;
}
.phone-tile-preview {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  grid-template-rows: 60px 69px 69px 69px;
  gap: 0;
  padding: 8px 10px 0;
  background: #061a74;
}
.phone-tile-page.is-light-tile .phone-tile-preview {
  background: transparent;
  padding: 8px 0 12px;
  grid-template-rows: 88px 97px 97px 97px;
}
.phone-tile-item {
  min-width: 0;
  margin: 3px;
  overflow: hidden;
  border-radius: 10px;
  color: #fff;
  background-color: #0b2c9c;
  background-position: center;
  background-repeat: no-repeat;
  background-size: 100% 100%;
  font-size: 11px;
  text-align: center;
}
.phone-tile-page.is-light-tile .phone-tile-item {
  margin: 4px;
}
.phone-tile-item.is-color-tile {
  position: relative;
  display: block;
  border-radius: 8px;
  box-shadow: 1px 2px 4px rgba(0, 0, 0, 0.2);
}
.phone-tile-title {
  display: flex;
  min-height: 100%;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
}
.phone-tile-item.is-color-tile .phone-tile-title {
  position: absolute;
  top: 8px;
  left: 8px;
  z-index: 2;
  display: block;
  min-height: auto;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  line-height: 1.25;
  text-align: left;
  white-space: nowrap;
}
.phone-tile-icon {
  position: absolute;
  right: 5px;
  bottom: 5px;
  z-index: 1;
  width: 42px;
  height: 42px;
  object-fit: contain;
}
.phone-tile-item.is-tall-color-tile .phone-tile-title {
  top: 12px;
  left: 12px;
  font-size: 18px;
}
.phone-tile-item.is-tall-color-tile .phone-tile-icon {
  right: 6px;
  bottom: 6px;
  width: 62px;
  height: 62px;
}
.phone-tile-footer {
  min-height: 52px;
  box-sizing: border-box;
  margin-top: 10px;
  padding: 12px 0;
  color: rgba(255, 255, 255, 0.92);
  background: rgba(0, 0, 0, 0.2);
  font-size: 12px;
  text-align: center;
}
.phone-tile-page.is-light-tile .phone-tile-footer {
  color: #666;
  background: transparent;
}
.phone-tile-footer-name {
  margin-left: 6px;
  font-weight: 600;
}
.phone-tile-footer-logo {
  width: 18px;
  height: 14px;
  margin-right: 6px;
  vertical-align: middle;
  object-fit: contain;
}
.phone-empty {
  grid-column: 1 / -1;
  padding: 40px 12px;
  color: #909399;
  font-size: 13px;
  text-align: center;
}
@media (max-width: 1200px) {
  .phone-preview-panel {
    flex-basis: 320px;
  }
  .phone-frame {
    width: 292px;
  }
  .phone-tile-preview {
    grid-template-rows: 52px 60px 60px 60px;
  }
  .phone-tile-page.is-light-tile .phone-tile-preview {
    grid-template-rows: 78px 86px 86px 86px;
  }
  .phone-tile-item.is-color-tile .phone-tile-title {
    font-size: 13px;
  }
  .phone-tile-icon {
    width: 38px;
    height: 38px;
  }
  .phone-tile-item.is-tall-color-tile .phone-tile-title {
    font-size: 16px;
  }
  .phone-tile-item.is-tall-color-tile .phone-tile-icon {
    width: 54px;
    height: 54px;
  }
}
</style>
