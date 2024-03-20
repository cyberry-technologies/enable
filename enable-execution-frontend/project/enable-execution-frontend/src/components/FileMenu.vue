
<template>
  <div class="file-menu" @mouseover="isHovered = true" @mouseleave="isHovered = false">
    <div class="circle" :class="{ 'expanded': isHovered }">
      <span class="hamburger-icon">☰</span>
      <div class="dropdown-menu" v-if="isHovered">
        <hr class="horizontal-line">

        <div class="circle-button button-icon inactive" data-tooltip="Profile"><BxSolidUser /></div>
        <div class="circle-button button-icon inactive" data-tooltip="Sign Out"><ReLogoutBoxLine /></div>

        <hr class="horizontal-line">

        <div class="circle-button button-icon" data-tooltip="New file" @click="newFile"><AnOutlinedFileAdd /></div>
        <div>
          <label for="file-upload" class="load-icon circle-button button-icon" data-tooltip="Load file"><AkCloudUpload /></label>
          <input type="file" id="file-upload" class="file-input" @change="handleFileChange">
        </div>
        <div class="circle-button button-icon" data-tooltip="Save file" @click="saveToFile"><BxSolidSave /></div>

        <hr class="horizontal-line">
        
        <div class="circle-button button-icon inactive" data-tooltip="Go to marketplace"><FaStore /></div>
        <div class="circle-button button-icon inactive" data-tooltip="Go to execution app"><CgSmartphoneChip /></div>
      </div>
    </div>
  </div>
</template>

<script>
import FileService from '@/services/FileService';

//icons
import { BxSolidUser } from "@kalimahapps/vue-icons";
import { ReLogoutBoxLine } from "@kalimahapps/vue-icons";
import { AnOutlinedFileAdd } from "@kalimahapps/vue-icons";
import { AkCloudUpload } from "@kalimahapps/vue-icons";
import { BxSolidSave } from "@kalimahapps/vue-icons";
import { FaStore } from "@kalimahapps/vue-icons";
import { CgSmartphoneChip } from "@kalimahapps/vue-icons";

export default {
  components: {
    BxSolidUser,
    ReLogoutBoxLine,
    AnOutlinedFileAdd,
    AkCloudUpload,
    BxSolidSave,
    FaStore,
    CgSmartphoneChip
  },
  data() {
    return {
      isHovered: false
    };
  },
  methods: {
    async handleFileChange(event) {
      const processFile = event.target.files[0];
      const jsonObject = await FileService.load(processFile);
      
      this.$emit("reloadData");
    },
    async saveToFile() {
      const processFile = await FileService.get();
      await FileService.save(processFile);

      this.$emit("reloadData");
    }
  }
}
</script>

<style scoped>

.file-menu {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 1002;
}

.circle {
  width: 40px;
  height: 40px;
  background-color: #333333;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: height 0.5s, border-radius 0s;
}

.expanded {
  width: 40px;
  height: 365px; /* 40 + 40*buttonCount */
  border-radius: 20px;
}

.hamburger-icon {
  color: #dddddd;
  fill: #dddddd;
  position: absolute;
  top: 2px;
  left: 10px;
  font-size: 22px;
}

.dropdown-menu {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;
  width: 100%;
  height: 100%;
  padding: 0px;
  position: absolute;
  margin-top: 8px;
}

.load-icon {
  width: 32px;
  height: 32px;
  background-color: #333333;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px solid #4e4e4e;
  position: relative;
  cursor: pointer;
  margin-bottom: 10px;
}

.load-icon:hover {
  background-color: #4e4e4e;
}

.file-input {
  display: none;
}

.circle-button {
  width: 32px;
  height: 32px;
  background-color: #333333;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px solid #4e4e4e;
  position: relative;
  cursor: pointer;
  margin-bottom: 10px;
}

.circle-button::before {
  content: attr(data-tooltip); /* Use the data-tooltip attribute as the tooltip text */
  position: absolute;
  top: 50%; /* Center the tooltip vertically */
  left: calc(100% + 5px); /* Position the tooltip to the right of the button */
  background-color: #4e4e4e;
  padding: 5px;
  border-radius: 5px;
  opacity: 0; /* Hide the tooltip by default */
  transition: opacity 0.3s ease, transform 0.3s ease; /* Add a transition for a smooth appearance */
  font-size: 11px; /* Set the font size of the tooltip */
  white-space: nowrap; /* Prevent the tooltip from wrapping to the next line */
  pointer-events: none; /* Prevent the tooltip from blocking mouse events */
}

.circle-button:hover::before {
  opacity: 1; /* Show the tooltip on hover */
  transform: translateY(-50%) translateX(5px); /* Move the tooltip to the right */
  transition-delay: 0.5s; /* Delay the appearance of the tooltip by 1 second */
}

.circle-button:hover {
  background-color: #4e4e4e;
}

.button-icon {
  color: #dddddd;
  font-size: 18px;
}

.inactive {
  color: #4e4e4e !important; /* Grey text color */
  pointer-events: none !important; /* Disable pointer events */
}

.horizontal-line {
  width: 80%;
  height: 1px;
  border: 1px solid #4e4e4e !important;
  margin-top: 0px;
  margin-bottom: 10px;
}

</style>

