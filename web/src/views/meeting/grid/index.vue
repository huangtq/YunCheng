<template>
  <div class="app-container">
    <div class="grid-workbench">
      <div class="grid-editor">
        <section class="template-config-card mb8">
          <div class="template-config-layout">
            <div class="template-config-versionbar">
              <span class="home-editor-status" :class="{ 'is-dirty': editorDirty }">{{ editorStatus }}</span>
              <div class="template-config-actions">
                <el-button
                  type="primary"
                  icon="DocumentChecked"
                  :loading="draftSaving"
                  @click="saveHomeDraft()"
                  v-hasPermi="['meeting:home:edit']"
                >
                  保存草稿
                </el-button>
                <el-button
                  type="success"
                  icon="Promotion"
                  :loading="publishing"
                  @click="publishHome"
                  v-hasPermi="['meeting:home:publish']"
                >
                  发布
                </el-button>
                <el-tooltip content="版本记录" placement="top">
                  <el-button
                    circle
                    icon="Clock"
                    aria-label="版本记录"
                    @click="loadHomeVersions(true, 1)"
                    v-hasPermi="['meeting:home:list']"
                  />
                </el-tooltip>
              </div>
            </div>
            <div class="template-config-controls">
              <span class="template-control-label">会议模板</span>
              <el-select
                v-model="configForm.gridTemplate"
                filterable
                class="template-select"
                aria-label="会议模板"
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
              <el-button plain icon="Setting" class="page-settings-button" @click="pageSettingsOpen = true">页面设置</el-button>
            </div>
          </div>
        </section>

        <el-drawer v-model="pageSettingsOpen" title="页面设置" direction="rtl" size="420px" append-to-body class="page-settings-drawer">
          <el-tabs v-model="pageSettingsTab" stretch>
            <el-tab-pane label="布局" name="layout">
              <div class="grid-visual-panel">
                <div class="grid-visual-header">
                  <div>
                    <div class="grid-visual-title">移动端布局</div>
                    <div class="grid-visual-hint">保存草稿后，变更才会进入待发布版本</div>
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
            </el-tab-pane>
            <el-tab-pane label="整体背景" name="background">
              <div class="grid-background-panel">
                <div class="grid-visual-header">
                  <div>
                    <div class="grid-visual-title">整体背景</div>
                    <div class="grid-visual-hint">选择一种背景类型，保存草稿后同步</div>
                  </div>
                </div>
                <el-radio-group v-model="gridBackgroundMode" class="grid-background-mode">
                  <el-radio-button value="color">纯色</el-radio-button>
                  <el-radio-button value="gradient">渐变</el-radio-button>
                  <el-radio-button value="image">图片</el-radio-button>
                </el-radio-group>
                <div v-if="gridBackgroundMode === 'color'" class="grid-background-field">
                  <span>背景底色</span>
                  <div class="grid-background-color-row">
                    <el-color-picker v-model="gridBackground.color" show-alpha />
                    <el-input v-model="gridBackground.color" placeholder="#f5f7fa" />
                  </div>
                </div>
                <div v-else-if="gridBackgroundMode === 'gradient'" class="grid-background-field">
                  <span>渐变背景</span>
                  <el-input v-model="gridBackground.gradient" type="textarea" :rows="2" placeholder="linear-gradient(135deg, #eef6ff, #ffffff)" />
                </div>
                <div v-else class="grid-background-field">
                  <span>背景图片</span>
                  <material-select v-model="gridBackground.imageUrl" :show-tip="false" />
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-drawer>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:grid:add']">新增</el-button>
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
          <div class="phone-screen" :class="{ 'is-tile-screen': isTilePreview }" :style="previewGridPageStyle">
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
                  <MeetingIcon
                    v-if="item.iconType === 'icon' && item.iconKey"
                    class="phone-tile-icon"
                    :icon-key="item.iconKey"
                    :size="previewIconSize(item)"
                    :style="previewTileIconStyle(item)"
                    color="#fff"
                  />
                  <img
                    v-else-if="isColorTile(item) && item.iconUrl"
                    class="phone-tile-icon"
                    :src="resolveUrl(item.iconUrl)"
                    :style="previewTileIconStyle(item)"
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
                    :style="previewCardImageStyle(item)"
                    alt=""
                  />
                  <template v-else>
                    <div class="phone-grid-icon" :style="previewGridIconStyle(item)">
                      <MeetingIcon
                        v-if="item.iconType === 'icon' && item.iconKey"
                        :icon-key="item.iconKey"
                        :size="previewIconSize(item)"
                        color="#fff"
                      />
                      <img
                        v-else-if="item.iconUrl"
                        :src="resolveUrl(item.iconUrl)"
                        :style="previewIconMediaStyle(item)"
                        alt=""
                      />
                      <el-icon v-else :size="previewIconSize(item)"><Grid /></el-icon>
                    </div>
                    <span v-if="!isIconOnlyPreview && gridShowTitle(item)">{{ item.title }}</span>
                  </template>
                </div>
                <div v-if="!previewItems.length" class="phone-empty">暂无启用的菜单项</div>
              </div>
            </template>
          </div>
        </div>
      </aside>
    </div>

    <el-drawer
      v-model="open"
      :title="title"
      direction="rtl"
      size="720px"
      class="grid-edit-drawer"
      append-to-body
      destroy-on-close
    >
      <el-scrollbar class="grid-edit-scrollbar">
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          class="grid-form"
          @click.capture="preventMobileFieldLabelClick"
        >
          <section class="grid-form-section">
            <h3>基础信息</h3>
            <el-form-item prop="title">
              <template #label>
                <span class="mobile-field-label">
                  标题
                  <el-tooltip content="显示为移动端首页入口名称，也会用于跳转页面的标题。" placement="top">
                    <el-icon class="mobile-field-help"><question-filled /></el-icon>
                  </el-tooltip>
                </span>
              </template>
              <el-input v-model="form.title" maxlength="100" placeholder="标题" />
            </el-form-item>
            <el-form-item prop="sortOrder">
              <template #label>
                <span class="mobile-field-label">
                  排序
                  <el-tooltip content="决定该入口在移动端首页的排列顺序，数字越小越靠前。" placement="top">
                    <el-icon class="mobile-field-help"><question-filled /></el-icon>
                  </el-tooltip>
                </span>
              </template>
              <el-input-number v-model="form.sortOrder" :min="0" :max="9999" controls-position="right" />
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
              <el-select v-model="form.moduleKey" placeholder="请选择模块" @change="onModuleChange">
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
              <el-input v-model="form.externalUrl" maxlength="500" placeholder="https://" />
            </el-form-item>
            <el-form-item v-if="form.linkType === 'content'" label="富文本内容">
              <editor
                v-model="form.content"
                :min-height="300"
                :upload-url="contentImageUploadUrl"
              />
            </el-form-item>
          </section>

          <section class="grid-form-section">
            <h3>展示设置</h3>
            <el-form-item>
              <template #label>
                <span class="mobile-field-label">
                  展示内容
                  <el-tooltip content="选择移动端首页入口使用图片素材还是图标库图标。" placement="top">
                    <el-icon class="mobile-field-help"><question-filled /></el-icon>
                  </el-tooltip>
                </span>
              </template>
              <el-radio-group v-model="form.iconType" @change="handleIconTypeChange">
                <el-radio-button value="image">图片素材</el-radio-button>
                <el-radio-button value="icon">图标库</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="form.iconType === 'image'" prop="iconUrl">
              <template #label>
                <span class="mobile-field-label">
                  素材图片
                  <el-tooltip content="普通宫格中作为入口图标；两列图文宫格中作为卡片图片；自定义宫格中可作为色块背景图。" placement="top">
                    <el-icon class="mobile-field-help"><question-filled /></el-icon>
                  </el-tooltip>
                </span>
              </template>
              <material-select v-model="form.iconUrl" :show-tip="false" />
            </el-form-item>
            <el-form-item v-else prop="iconKey">
              <template #label>
                <span class="mobile-field-label">
                  图标选择
                  <el-tooltip content="选择移动端首页展示的入口图标。" placement="top">
                    <el-icon class="mobile-field-help"><question-filled /></el-icon>
                  </el-tooltip>
                </span>
              </template>
              <meeting-icon-select v-model="form.iconKey" />
            </el-form-item>
            <el-form-item v-if="showIconSurfaceControls">
              <template #label>
                <span class="mobile-field-label">
                  展示尺寸
                  <el-tooltip content="设置入口图标或素材在移动端中的显示大小。" placement="top">
                    <el-icon class="mobile-field-help"><question-filled /></el-icon>
                  </el-tooltip>
                </span>
              </template>
              <el-slider v-model="form.iconSize" :min="20" :max="48" :step="2" show-input class="icon-style-slider" />
            </el-form-item>
            <el-form-item v-if="showIconSurfaceControls">
              <template #label>
                <span class="mobile-field-label">
                  背景颜色
                  <el-tooltip content="设置图标或素材外层色块；留空时跟随会议主题色。" placement="top">
                    <el-icon class="mobile-field-help"><question-filled /></el-icon>
                  </el-tooltip>
                </span>
              </template>
              <div class="icon-background-control">
                <el-color-picker v-model="form.iconBackground" show-alpha />
                <el-input v-model="form.iconBackground" clearable placeholder="跟随主题色" />
              </div>
            </el-form-item>
            <el-form-item>
              <template #label>
                <span class="mobile-field-label">
                  展示圆角
                  <el-tooltip :content="isImageCardForm ? '设置两列素材卡片的圆角。' : '设置图标或素材外层色块的圆角。'" placement="top">
                    <el-icon class="mobile-field-help"><question-filled /></el-icon>
                  </el-tooltip>
                </span>
              </template>
              <el-slider v-model="form.iconRadius" :min="0" :max="24" :step="1" show-input class="icon-style-slider" />
            </el-form-item>
            <el-form-item v-if="isImageCardForm">
              <template #label>
                <span class="mobile-field-label">
                  素材比例
                  <el-tooltip content="原图按图片自身比例展示；固定比例会居中裁切素材。" placement="top">
                    <el-icon class="mobile-field-help"><question-filled /></el-icon>
                  </el-tooltip>
                </span>
              </template>
              <el-radio-group v-model="form.imageRatio" class="image-ratio-group">
                <el-radio-button value="auto">原图</el-radio-button>
                <el-radio-button value="1:1">1:1</el-radio-button>
                <el-radio-button value="4:3">4:3</el-radio-button>
                <el-radio-button value="16:9">16:9</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <template #label>
                <span class="mobile-field-label">
                  显示状态
                  <el-tooltip content="开启隐藏后，该入口不会出现在移动端发布页面中。" placement="top">
                    <el-icon class="mobile-field-help"><question-filled /></el-icon>
                  </el-tooltip>
                </span>
              </template>
              <el-switch v-model="form.hidden" active-text="隐藏此菜单" />
            </el-form-item>
            <template v-if="isTilePreview">
              <el-divider content-position="left">Tile 色块与布局</el-divider>
              <el-form-item>
                <template #label>
                  <span class="mobile-field-label">
                    色块背景
                    <el-tooltip content="仅自定义宫格使用，设置该入口在移动端首页的纯色或渐变背景。" placement="top">
                      <el-icon class="mobile-field-help"><question-filled /></el-icon>
                    </el-tooltip>
                  </span>
                </template>
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
                  <el-form-item>
                    <template #label>
                      <span class="mobile-field-label">
                        行
                        <el-tooltip content="仅自定义宫格使用，设置入口起始行；填 0 时由移动端自动排列。" placement="top">
                          <el-icon class="mobile-field-help"><question-filled /></el-icon>
                        </el-tooltip>
                      </span>
                    </template>
                    <el-input-number v-model="form.tileRow" :min="0" :max="20" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item>
                    <template #label>
                      <span class="mobile-field-label">
                        列
                        <el-tooltip content="仅自定义宫格使用，设置入口起始列；填 0 时由移动端自动排列。" placement="top">
                          <el-icon class="mobile-field-help"><question-filled /></el-icon>
                        </el-tooltip>
                      </span>
                    </template>
                    <el-input-number v-model="form.tileCol" :min="0" :max="6" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item>
                    <template #label>
                      <span class="mobile-field-label">
                        跨行
                        <el-tooltip content="仅自定义宫格使用，决定入口在移动端首页纵向占用的网格数量。" placement="top">
                          <el-icon class="mobile-field-help"><question-filled /></el-icon>
                        </el-tooltip>
                      </span>
                    </template>
                    <el-input-number v-model="form.tileRowSpan" :min="1" :max="6" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item>
                    <template #label>
                      <span class="mobile-field-label">
                        跨列
                        <el-tooltip content="仅自定义宫格使用，决定入口在移动端首页横向占用的网格数量。" placement="top">
                          <el-icon class="mobile-field-help"><question-filled /></el-icon>
                        </el-tooltip>
                      </span>
                    </template>
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
    </el-drawer>

    <el-dialog v-model="versionsOpen" title="会议页版本记录" width="760px" append-to-body>
      <el-table :data="homeVersions" v-loading="versionsLoading">
        <el-table-column prop="versionNo" label="版本" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'published' ? 'success' : scope.row.status === 'draft' ? 'warning' : 'info'">
              {{ scope.row.status === 'published' ? '已发布' : scope.row.status === 'draft' ? '草稿' : '已归档' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishRemark" label="发布备注" min-width="180" show-overflow-tooltip />
        <el-table-column prop="publishedTime" label="发布时间" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button
              link
              type="primary"
              :loading="restoringVersionId === scope.row.versionId"
              :disabled="scope.row.versionId === homeVersionId"
              @click="restoreHomeVersion(scope.row)"
              v-hasPermi="['meeting:home:edit']"
            >
              {{ scope.row.versionId === homeVersionId ? "当前草稿" : "覆盖草稿" }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="version-pagination">
        <el-pagination
          v-model:current-page="versionsPage"
          v-model:page-size="versionsPageSize"
          :total="versionsTotal"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handleVersionsPageChange"
          @size-change="handleVersionsSizeChange"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="MeetingGrid">
import { listGrid, getGrid, addGrid, updateGrid, delGrid } from "@/api/meeting/grid"
import { getActivity } from "@/api/meeting/activity"
import { getActivityConfig } from "@/api/meeting/config"
import MaterialSelect from "@/components/MaterialSelect"
import MeetingIcon from "@/components/MeetingIcon"
import MeetingIconSelect from "@/components/MeetingIconSelect"
import { MEETING_MODULE_OPTIONS, getMeetingModule, meetingModuleLabel } from "@/utils/meetingModules"
import { listHomeVersions, publishHomeVersion, restoreHomeVersion as restoreHomeVersionApi, saveHomeDraft as saveHomeDraftVersion } from "@/api/meeting/homeVersion"
import { getMeetingHomeTemplate } from "@/utils/meetingHomeTemplates"

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()
const baseUrl = import.meta.env.VITE_APP_BASE_API

const activityId = computed(() => route.query.id)
const contentImageUploadUrl = computed(() => activityId.value
  ? `${baseUrl}/meeting/activity/${activityId.value}/file/upload`
  : "")
const activityInfo = ref({})
const loading = ref(true)
const gridList = ref([])
const total = ref(0)
const ids = ref([])
const multiple = ref(true)
const open = ref(false)
const title = ref("")
const configForm = ref({ gridTemplate: "1" })
const DEFAULT_GRID_BACKGROUND_COLOR = "#ffffff"
const gridVisual = reactive({
  heroHeight: 0,
  countdownTop: 16,
  countdownBottom: 20,
  itemGap: 10,
  itemPadding: 10
})
const gridBackground = reactive({ color: DEFAULT_GRID_BACKGROUND_COLOR, gradient: "", imageUrl: "" })
const gridBackgroundMode = ref("color")
const pageSettingsOpen = ref(false)
const pageSettingsTab = ref("layout")
const activeGridBackground = computed(() => ({
  color: gridBackgroundMode.value === "color" ? gridBackground.color : "",
  gradient: gridBackgroundMode.value === "gradient" ? gridBackground.gradient : "",
  imageUrl: gridBackgroundMode.value === "image" ? gridBackground.imageUrl : ""
}))
const homeVersionId = ref(null)
const homeVersions = ref([])
const versionsOpen = ref(false)
const versionsLoading = ref(false)
const versionsPage = ref(1)
const versionsPageSize = ref(10)
const versionsTotal = ref(0)
const restoringVersionId = ref(null)
const draftSaving = ref(false)
const publishing = ref(false)
const editorLoaded = ref(false)
const editorDirty = ref(false)

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
    label: "自定义宫格",
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

const publishedHomeVersion = computed(() => homeVersions.value.find(item => item.status === "published"))
const editorStatus = computed(() => {
  if (!editorLoaded.value) return "正在加载编辑器"
  if (editorDirty.value) return "草稿未保存"
  if (homeVersionId.value) return "草稿已保存，待发布"
  if (publishedHomeVersion.value) return `当前线上 v${publishedHomeVersion.value.versionNo}`
  return "尚未创建草稿"
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
const gridBackgroundPreviewStyle = computed(() => {
  const style = { backgroundColor: gridBackground.color || DEFAULT_GRID_BACKGROUND_COLOR }
  if (gridBackgroundMode.value === "color") {
    style.backgroundColor = gridBackground.color || DEFAULT_GRID_BACKGROUND_COLOR
  } else if (gridBackgroundMode.value === "gradient") {
    style.backgroundImage = gridBackground.gradient || "none"
  } else if (gridBackground.imageUrl) {
    style.backgroundImage = `url("${resolveUrl(gridBackground.imageUrl)}")`
    style.backgroundSize = "cover"
  }
  return style
})
const isTwoColumnPreview = computed(() => ["68", "681"].includes(String(configForm.value.gridTemplate)))
const isImageCardForm = computed(() => isTwoColumnPreview.value && form.value.iconType === "image")
const showIconSurfaceControls = computed(() => (
  !isImageCardForm.value
  && (!isTilePreview.value || form.value.iconType === "icon" || !!form.value.gradientColor)
))

const DEFAULT_ICON_SIZE = 28
const DEFAULT_ICON_RADIUS = 12

function clampNumber(value, min, max, fallback) {
  const parsed = Number(value)
  if (!Number.isFinite(parsed)) return fallback
  return Math.min(max, Math.max(min, parsed))
}

function previewIconSize(item) {
  return clampNumber(item?.iconSize, 20, 48, DEFAULT_ICON_SIZE)
}

function previewGridIconStyle(item) {
  const size = previewIconSize(item)
  const surfaceSize = isIconOnlyPreview.value
    ? Math.max(60, size + 28)
    : Math.max(44, size + 16)
  return {
    width: `${surfaceSize}px`,
    height: `${surfaceSize}px`,
    borderRadius: `${clampNumber(item?.iconRadius, 0, 24, DEFAULT_ICON_RADIUS)}px`,
    background: item?.iconBackground || previewIconSurfaceColor.value
  }
}

function previewIconMediaStyle(item) {
  const size = previewIconSize(item)
  return { width: `${size}px`, height: `${size}px` }
}

function normalizedImageRatio(value) {
  return ["1:1", "4:3", "16:9"].includes(value) ? value : "auto"
}

function previewCardImageStyle(item) {
  const ratio = normalizedImageRatio(item?.imageRatio)
  const style = {
    borderRadius: `${clampNumber(item?.iconRadius, 0, 24, DEFAULT_ICON_RADIUS)}px`
  }
  if (ratio !== "auto") {
    style.aspectRatio = ratio.replace(":", " / ")
    style.objectFit = "cover"
  }
  return style
}

function previewTileIconStyle(item) {
  const size = previewIconSize(item)
  return { width: `${size}px`, height: `${size}px` }
}

function isImageCardPreview(item) {
  return item && item.iconType === "image" && !!gridCardUrl(item) && previewGridClass.value === "grid-two"
}

function gridCardUrl(item) {
  return item?.contentUrl || item?.iconUrl || ""
}

function preventMobileFieldLabelClick(event) {
  if (event.target?.closest?.(".mobile-field-label")) {
    event.preventDefault()
  }
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
    gridRow: `${item.tileRow || "auto"} / span ${item.tileRowSpan || 1}`,
    borderRadius: `${clampNumber(item?.iconRadius, 0, 24, DEFAULT_ICON_RADIUS)}px`
  }
  const gradient = meta.bg || meta.background || meta.tileBg || meta.gradientColor
  if (gradient) {
    style.backgroundImage = gradient
    style.backgroundColor = "transparent"
  } else if (item.iconUrl) {
    style.backgroundImage = `url("${resolveUrl(item.iconUrl)}")`
  } else if (item.iconBackground) {
    style.backgroundColor = item.iconBackground
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
const previewGridPageStyle = computed(() => ({
  ...previewGridVisualStyle.value,
  ...gridBackgroundPreviewStyle.value
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
    gridList.value = (res.rows || []).map(item => {
      const options = parseGridOptions(item.remark)
      return {
        ...item,
        iconSize: clampNumber(options.iconSize, 20, 48, DEFAULT_ICON_SIZE),
        iconBackground: options.iconBackground || "",
        iconRadius: clampNumber(options.iconRadius, 0, 24, DEFAULT_ICON_RADIUS),
        imageRatio: normalizedImageRatio(options.imageRatio)
      }
    })
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}

function markEditorDirty() {
  if (editorLoaded.value) editorDirty.value = true
}

function loadMeta() {
  getActivity(activityId.value).then(res => {
    activityInfo.value = res.data || {}
  })
  getActivityConfig(activityId.value).then(res => {
    const config = res.data || {}
    configForm.value = {
      ...configForm.value,
      ...config,
      gridTemplate: normalizeTemplate(config.gridTemplate)
    }
    Object.assign(gridVisual, parseGridVisual(config.remark))
    Object.assign(gridBackground, parseGridBackground(config.remark, config.mobileBackgroundUrl))
    gridBackgroundMode.value = resolveGridBackgroundMode(gridBackground)
  }).finally(() => {
    nextTick(() => {
      editorLoaded.value = true
      editorDirty.value = false
    })
  })
}

watch(() => configForm.value.gridTemplate, markEditorDirty)
watch(gridVisual, markEditorDirty, { deep: true })
watch(gridBackground, markEditorDirty, { deep: true })
watch(gridBackgroundMode, markEditorDirty)

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

function gridShowTitle(item) {
  return item?.showTitle !== false && item?.showTitle !== 0 && item?.showTitle !== "0"
}

function parseGridBackground(value, mobileBackgroundUrl = "") {
  const defaults = { color: DEFAULT_GRID_BACKGROUND_COLOR, gradient: "", imageUrl: "" }
  try {
    const parsed = JSON.parse(value || "{}")
    return {
      ...defaults,
      ...(parsed.gridBackground || {}),
      imageUrl: parsed.gridBackground?.imageUrl || mobileBackgroundUrl || ""
    }
  } catch {
    return { ...defaults, imageUrl: mobileBackgroundUrl || "" }
  }
}

function resolveGridBackgroundMode(background) {
  if (background.imageUrl) return "image"
  if (background.gradient) return "gradient"
  return "color"
}

function buildConfigRemark() {
  let parsed = {}
  try {
    parsed = JSON.parse(configForm.value.remark || "{}")
  } catch {}
  return JSON.stringify({ ...parsed, gridVisual: { ...gridVisual }, gridBackground: activeGridBackground.value })
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

function homeTemplateKey() {
  if (configForm.value.mobileTemplate === "image-map") return "sciconf-poster-map"
  if (String(configForm.value.gridTemplate) === "tile") return "sciconf-tile-service"
  if (["68", "681"].includes(String(configForm.value.gridTemplate))) return "sciconf-image-menu"
  if (["5", "71"].includes(String(configForm.value.gridTemplate))) return "sciconf-icon-grid"
  return "sciconf-icon-grid"
}

function homeEntryTarget(item) {
  if (item.linkType === "module") return { targetType: "module", target: { moduleKey: item.moduleKey || "" } }
  if (item.linkType === "url") return { targetType: "external", target: { url: item.externalUrl || "" } }
  if (item.linkType === "phone") return { targetType: "phone", target: { phone: item.phone || item.externalUrl || "" } }
  if (item.linkType === "content") {
    return {
      targetType: "content",
      target: item.contentId ? { contentId: item.contentId } : { contentId: null },
      legacyContent: item.content || "",
      contentType: item.contentType || "text",
      contentUrl: item.contentUrl || ""
    }
  }
  return { targetType: "group", target: {}, children: [{ id: `grid-${item.gridId}-placeholder`, title: "未配置动作", enabled: false, targetType: "module", target: { moduleKey: "apply" } }] }
}

function parseHomeBlocks() {
  try {
    const blocks = JSON.parse(configForm.value.mobileBlocksJson || "[]")
    return Array.isArray(blocks) ? blocks : []
  } catch {
    return []
  }
}

function buildHomePage() {
  const templateKey = homeTemplateKey()
  const template = getMeetingHomeTemplate(templateKey)
  const sectionKey = template.layout.gridTemplate === "tile" ? "tiles" : template.layout.template === "image-map" ? "hotspots" : "menu"
  const sourceItems = template.layout.template === "image-map" && parseHomeBlocks().length
    ? parseHomeBlocks()
    : previewItems.value.filter(item => ["module", "url", "content"].includes(item.linkType))
  const entries = sourceItems.map((item, index) => ({
    id: `grid-${item.gridId || item.id || index}`,
    title: item.title || "",
    enabled: String(item.status) !== "0",
    sort: item.sortOrder || index,
    sectionKey,
    iconType: item.iconType || "image",
    iconKey: item.iconKey || "",
    iconUrl: item.iconUrl || "",
    iconSize: clampNumber(item.iconSize, 20, 48, DEFAULT_ICON_SIZE),
    iconBackground: item.iconBackground || "",
    iconRadius: clampNumber(item.iconRadius, 0, 24, DEFAULT_ICON_RADIUS),
    imageRatio: normalizedImageRatio(item.imageRatio),
    contentId: item.contentId || null,
    displayAsCard: item.displayAsCard === true,
    tileRow: item.tileRow || 0,
    tileCol: item.tileCol || 0,
    tileRowSpan: item.tileRowSpan || 1,
    tileColSpan: item.tileColSpan || 1,
    showTitle: item.showTitle !== false,
    bounds: item.bounds || {
      left: item.left,
      top: item.top,
      width: item.width,
      height: item.height
    },
    ...homeEntryTarget(item)
  }))
  const layout = {
    ...JSON.parse(JSON.stringify(template.layout)),
    template: configForm.value.mobileTemplate || template.layout.template,
    heroUrl: activityInfo.value.coverUrl || "",
    backgroundUrl: activeGridBackground.value.imageUrl,
    background: { ...activeGridBackground.value },
    themeColor: themeColor.value,
    gridTemplate: configForm.value.gridTemplate || "1",
    gridColumns: previewGridClass.value === "grid-one" ? 1 : previewGridClass.value === "grid-two" ? 2 : 3,
    gridStyle: isIconOnlyPreview.value ? "icon" : hasImageCardPreview.value ? "image-card" : "icon-text",
    showCountdown: String(configForm.value.showCountdown) === "1",
    countdownStyle: configForm.value.countdownStyle || "classic",
    showRegisterCount: String(configForm.value.showRegisterCount) === "1",
    notice: configForm.value.mobileNotice || "",
    audioUrl: configForm.value.audioUrl || "",
    audioAutoplay: String(configForm.value.audioAutoplay) === "1",
    audioLoop: String(configForm.value.audioLoop) !== "0",
    footer: {
      enabled: String(configForm.value.footerEnabled) === "1",
      text: configForm.value.footerText || "",
      company: configForm.value.footerCompany || "",
      logoUrl: configForm.value.footerLogoUrl || "",
      linkUrl: configForm.value.footerLinkUrl || ""
    },
    visual: { ...gridVisual }
  }
  layout.entrySections = [{ key: sectionKey, label: sectionKey === "tiles" ? "服务入口" : sectionKey === "hotspots" ? "海报热点" : "会议菜单", columns: layout.gridColumns, ratio: layout.gridStyle === "image-card" ? "wide" : "square", min: 0, max: entries.length || 20 }]
  return {
    source: "grid-config",
    mode: "standard",
    schemaVersion: "2",
    templateKey,
    theme: { color: themeColor.value, background: { ...activeGridBackground.value } },
    layout,
    sections: template.slots.map(slot => ({
      id: slot,
      type: slot,
      enabled: true,
      entries: slot === sectionKey ? entries : []
    })),
    entryTree: entries
  }
}

async function saveHomeDraft(options = {}) {
  const silent = options.silent === true
  const pageJson = JSON.stringify(buildHomePage())
  const configRemark = buildConfigRemark()
  const mobileBackgroundUrl = activeGridBackground.value.imageUrl
  draftSaving.value = true
  try {
    const res = await saveHomeDraftVersion({
      versionId: homeVersionId.value,
      activityId: Number(activityId.value),
      pageJson,
      schemaVersion: "2",
      gridTemplate: configForm.value.gridTemplate,
      configRemark,
      mobileBackgroundUrl
    })
    homeVersionId.value = res.data?.versionId
    configForm.value.remark = configRemark
    configForm.value.mobileBackgroundUrl = mobileBackgroundUrl
    editorDirty.value = false
    await loadHomeVersions(false, 1)
    if (!silent) proxy.$modal.msgSuccess("草稿已保存")
    return res.data
  } finally {
    draftSaving.value = false
  }
}

async function publishHome() {
  try {
    if (editorDirty.value || !homeVersionId.value) {
      await saveHomeDraft({ silent: true })
    }
    if (!homeVersionId.value) return
    const { value } = await proxy.$modal.prompt("填写发布备注（可选）", "发布会议页")
    publishing.value = true
    await publishHomeVersion(homeVersionId.value, value)
    homeVersionId.value = null
    editorDirty.value = false
    await loadHomeVersions(false, 1)
    proxy.$modal.msgSuccess("会议页已发布")
  } catch {
    // Dialog cancellation and request failures are handled by the shared modal/request layers.
  } finally {
    publishing.value = false
  }
}

async function loadHomeVersions(openDialog = true, pageNum = versionsPage.value) {
  versionsLoading.value = true
  try {
    const res = await listHomeVersions({
      activityId: Number(activityId.value),
      pageNum,
      pageSize: versionsPageSize.value
    })
    homeVersions.value = res.rows || []
    versionsPage.value = pageNum
    versionsTotal.value = Number(res.total || 0)
    if (pageNum === 1) {
      const draft = homeVersions.value.find(item => item.status === "draft" && String(item.pageJson || "").includes('"source":"grid-config"'))
      homeVersionId.value = draft ? draft.versionId : null
    }
    if (openDialog) versionsOpen.value = true
  } catch (error) {
    homeVersions.value = []
    versionsTotal.value = 0
    if (openDialog) {
      const message = String(error?.message || "")
      proxy.$modal.msgWarning(message.includes("yc_activity_home_version")
        ? "版本记录表尚未初始化，请先执行会议首页迁移脚本"
        : "版本记录加载失败")
    }
  } finally {
    versionsLoading.value = false
  }
}

function handleVersionsPageChange(pageNum) {
  loadHomeVersions(false, pageNum)
}

function handleVersionsSizeChange(pageSize) {
  versionsPageSize.value = pageSize
  loadHomeVersions(false, 1)
}

function restoreHomeVersion(row) {
  const versionNo = row?.versionNo || "未知"
  proxy.$modal.confirm(`确认将版本 v${versionNo} 的内容覆盖到当前草稿吗？当前草稿中未保存的内容会被替换。`).then(async () => {
    restoringVersionId.value = row.versionId
    try {
      const res = await restoreHomeVersionApi(row.versionId)
      homeVersionId.value = res.data?.versionId || null
      editorDirty.value = false
      await loadHomeVersions(false, 1)
      proxy.$modal.msgSuccess(`版本 v${versionNo} 已覆盖到当前草稿`)
    } finally {
      restoringVersionId.value = null
    }
  }).catch(() => {})
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
  const legacyImageContent = data.contentType === "image" && data.contentUrl
    ? `<p><img src="${resolveUrl(data.contentUrl)}" /></p>`
    : ""
  return {
    ...data,
    iconType: data.iconType || (data.iconKey ? "icon" : "image"),
    iconKey: data.iconKey || "",
    content: data.content || options.content || legacyImageContent,
    contentType: "text",
    contentUrl: data.contentUrl || "",
    tileRow: data.tileRow || 0,
    tileCol: data.tileCol || 0,
    tileRowSpan: data.tileRowSpan || 1,
    tileColSpan: data.tileColSpan || 1,
    gradientColor: options.gradientColor || tileBg || "",
    tileBg,
    iconSize: clampNumber(options.iconSize, 20, 48, DEFAULT_ICON_SIZE),
    iconBackground: options.iconBackground || "",
    iconRadius: clampNumber(options.iconRadius, 0, 24, DEFAULT_ICON_RADIUS),
    imageRatio: normalizedImageRatio(options.imageRatio),
    hidden: options.hidden !== undefined
      ? options.hidden === true || options.hidden === 1 || options.hidden === "1"
      : String(data.status) === "0",
    displayAsCard: options.displayAsCard === true || options.displayAsCard === 1 || options.displayAsCard === "1",
    showTitle: options.showTitle !== false && options.showTitle !== 0 && options.showTitle !== "0"
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
  delete payload.iconSize
  delete payload.iconBackground
  delete payload.iconRadius
  delete payload.imageRatio
  payload.status = form.value.hidden ? "0" : "1"
  payload.iconType = form.value.iconType || "image"
  payload.iconUrl = payload.iconType === "image" ? (form.value.iconUrl || "") : ""
  payload.iconKey = payload.iconType === "icon" ? (form.value.iconKey || "") : ""
  payload.showTitle = form.value.showTitle !== false
  payload.contentType = "text"
  payload.contentUrl = ""
  const bg = String(form.value.gradientColor || form.value.tileBg || "").trim()
  const remarkObj = { __gridForm: true }
  remarkObj.iconSize = clampNumber(form.value.iconSize, 20, 48, DEFAULT_ICON_SIZE)
  remarkObj.iconBackground = form.value.iconBackground || ""
  remarkObj.iconRadius = clampNumber(form.value.iconRadius, 0, 24, DEFAULT_ICON_RADIUS)
  remarkObj.imageRatio = normalizedImageRatio(form.value.imageRatio)
  if (form.value.displayAsCard) remarkObj.displayAsCard = true
  if (form.value.showTitle === false) remarkObj.showTitle = false
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
    iconSize: DEFAULT_ICON_SIZE,
    iconBackground: "",
    iconRadius: DEFAULT_ICON_RADIUS,
    imageRatio: "auto",
    displayAsCard: false,
    showTitle: true,
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
      markEditorDirty()
    })
  })
}

function handleDelete(row) {
  const gridIds = row?.gridId || ids.value
  proxy.$modal.confirm("是否确认删除选中的九宫格项？").then(() => delGrid(gridIds)).then(() => {
    getList()
    markEditorDirty()
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
  loadHomeVersions(false, 1)
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
.template-config-card {
  margin-bottom: 16px;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f8fafc;
}
.template-config-layout {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.template-config-versionbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.template-config-controls {
  display: flex;
  min-width: 0;
  align-items: center;
  gap: 10px;
  padding-top: 12px;
  border-top: 1px solid #e3e8ef;
}
.template-control-label {
  flex: 0 0 auto;
  color: #526174;
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
}
.home-editor-status {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 10px;
  border: 1px solid #dfe5ec;
  border-radius: 999px;
  background: #f8fafc;
  color: #526174;
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
}
.home-editor-status.is-dirty {
  border-color: #f3d19e;
  background: #fdf6ec;
  color: #b7791f;
}
.template-config-actions {
  display: flex;
  align-items: center;
  gap: 6px;
}
.version-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
.template-select {
  flex: 1 1 auto;
  min-width: 140px;
  max-width: 300px;
}
.page-settings-button {
  color: #526174;
  border-color: #d9e1ea;
  background: #fff;
}
.page-settings-button:hover {
  color: #1f6feb;
  border-color: #9fc5ee;
  background: #f3f8ff;
}
@media (max-width: 760px) {
  .template-config-card {
    padding: 14px;
  }
  .template-config-layout {
    gap: 10px;
  }
  .template-config-versionbar {
    align-items: flex-start;
    flex-direction: column;
  }
  .template-config-controls {
    flex-wrap: wrap;
  }
  .template-select {
    max-width: none;
  }
  .template-config-actions {
    flex-wrap: wrap;
  }
}
.page-settings-drawer :deep(.el-drawer__header) {
  margin-bottom: 0;
  padding: 18px 22px;
  border-bottom: 1px solid #ebeef5;
  color: #1f2937;
  font-size: 16px;
  font-weight: 600;
}
.page-settings-drawer :deep(.el-drawer__body) {
  padding: 0 22px 24px;
}
.page-settings-drawer :deep(.el-tabs__header) {
  margin-bottom: 18px;
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
  padding: 0 2px 8px;
}

.grid-edit-scrollbar {
  height: 100%;
}
.grid-edit-drawer :deep(.el-drawer__header) {
  margin-bottom: 0;
  padding: 18px 24px;
  border-bottom: 1px solid #ebeef5;
}
.grid-edit-drawer :deep(.el-drawer__title) {
  color: #1f2937;
  font-size: 17px;
  font-weight: 600;
}
.grid-edit-drawer :deep(.el-drawer__body) {
  padding: 18px 22px 8px;
  background: #f5f7fa;
}
.grid-edit-drawer :deep(.el-drawer__footer) {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 22px;
  border-top: 1px solid #ebeef5;
  background: #fff;
}
.grid-edit-drawer :deep(.el-drawer__footer .el-button + .el-button) {
  margin-left: 0;
}
.grid-form-section {
  margin-bottom: 16px;
  padding: 18px 20px 6px;
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
  margin: 0 0 18px;
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
.grid-form-section :deep(.el-form-item:last-child) { margin-bottom: 0; }
.grid-form-section :deep(.el-row:last-child .el-form-item) { margin-bottom: 0; }
.grid-form-section :deep(.el-form-item__label) {
  color: #606266;
  padding-right: 12px;
  line-height: 22px;
  font-weight: 600;
}
.mobile-field-label {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  max-width: 100%;
}
.mobile-field-help {
  position: relative;
  top: -1px;
  flex: 0 0 auto;
  color: #909399;
  cursor: help;
  font-size: 15px;
  line-height: 1;
  transition: color 0.2s;
}
.mobile-field-help:hover {
  color: #409eff;
}
.grid-form-section :deep(.el-form-item) {
  margin-bottom: 18px;
}
.grid-form-section :deep(.el-form-item__content) {
  min-height: 32px;
  line-height: 32px;
}
.grid-form-section :deep(.el-row) { row-gap: 0; }
.grid-form-section :deep(.el-input),
.grid-form-section :deep(.el-select),
.grid-form-section :deep(.el-input-number) { width: 100%; max-width: 100%; }
.grid-form-section :deep(.el-input-number) {
  width: 130px;
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
.icon-style-slider {
  width: min(100%, 560px);
}
.icon-background-control {
  display: flex;
  align-items: center;
  gap: 10px;
  width: min(100%, 360px);
}
.icon-background-control .el-input {
  flex: 1;
}
.image-ratio-group {
  display: flex;
  flex-wrap: wrap;
}
.module-tip {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
  color: #909399;
  font-size: 13px;
}
.tile-bg-editor {
  width: min(100%, 620px);
  display: flex;
  flex-direction: column;
  gap: 12px;
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
  margin: 6px 0 18px;
}
.tile-bg-editor {
  width: min(100%, 560px);
}
.tile-bg-toolbar {
  flex-wrap: wrap;
}
.grid-form-section :deep(.el-form-item + .el-divider) { margin-top: 2px; }
.grid-form-section :deep(.el-divider + .el-form-item) { margin-top: 0; }
.grid-background-panel { display: flex; flex-direction: column; gap: 14px; }
.grid-background-mode { margin-bottom: 2px; }
.grid-background-field { display: flex; flex-direction: column; gap: 6px; color: #606266; font-size: 13px; font-weight: 600; }
.grid-background-color-row { display: flex; align-items: center; gap: 8px; }
.grid-background-color-row .el-input { flex: 1; }
@media (max-width: 700px) {
  .grid-edit-drawer { width: min(100vw, 720px) !important; }
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
  .grid-form-section { padding: 16px 14px 4px; }
  .grid-form-section h3 { margin-bottom: 16px; }
  .grid-form-section :deep(.el-form-item) { margin-bottom: 16px; }
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
  background: transparent;
  scrollbar-width: thin;
}
.phone-screen.is-tile-screen {
  background: transparent;
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
  position: relative;
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
