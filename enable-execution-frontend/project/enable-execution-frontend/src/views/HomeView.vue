<template>
  <main>
    <MainCanvas 
      ref="mainCanvas" 
      @reloadData="reloadDataInComponents" 
      @resetData="resetData"/>
    <FileMenu 
      ref="fileMenu" 
      @reloadData="reloadDataInComponents" 
      @resetData="resetData"/>
    <TopBar 
      ref="topBar" 
      @reloadData="reloadDataInComponents" 
      @resetData="resetData"/>
    <InfoMenu 
      ref="infoMenu" 
      @reloadData="reloadDataInComponents"  
      @resetData="resetData" 
      @openProcessInfoPopup="openProcessInfoPopup"/>
    <ProcessInfoPopup 
      ref="processInfoPopup" 
      v-if="processInfoPopupOpen" 
      @reloadData="reloadDataInComponents" 
      @resetData="resetData" 
      @close="closeProcessInfoPopup" />
  </main>
</template>

<script>
import FileMenu from '../components/FileMenu.vue'
import InfoMenu from '../components/InfoMenu.vue'
import ProcessInfoPopup from '../components/ProcessInfoPopup.vue'

import FileService from '@/services/FileService';
import ContentService from '@/services/ContentService';
import NavigationService from '@/services/NavigationService';

export default {
  components: {
    FileMenu,
    InfoMenu,
    ProcessInfoPopup
  },
  data() {
    return {
      processInfoPopupOpen: null,
    };
  },
  methods: {
    async reloadDataInComponents() {
      await this.$refs.infoMenu.loadData();
      this.closeProcessInfoPopup();
    },
    async resetData() {
      await FileService.resetOpenedAndSelectedProcessId();

      const openedProcessId = await NavigationService.getOpenedProcessId();
      const selectedProcessId = await NavigationService.getSelectedProcessId();
      
      if(!openedProcessId || !selectedProcessId) {
        console.log("Process file corrupt. No provided opened, selected or main process id.");
        return;
      }

      if(!ContentService.getProcessInfo(openedProcessId) || !ContentService.getProcessInfo(selectedProcessId)) {
        console.log("Process file corrupt. Opened, selected or main process id could not be linked to process in file.");
        return;
      }

      window.location.reload();
    },
    openProcessInfoPopup() {
      this.processInfoPopupOpen = true;
    },
    closeProcessInfoPopup() {
      this.processInfoPopupOpen = false;
    }
  }
}
</script>

<style scoped>
.body {
  background-color: #1A1A1A;
}
</style>
