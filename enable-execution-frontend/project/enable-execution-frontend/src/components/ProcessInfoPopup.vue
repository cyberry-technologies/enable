<template>
  <div class="popup-overlay" @click.self="closePopup">
    <div class="popup-content">
      <span class="close-button" @click="closePopup">X</span>
      <div class="process-info-popup">
        <div class="variable-block">
          <label for="name">Name:</label>
          <input type="text" id="name" v-model="process.name">
        </div>
        <div class="variable-block">
          <label for="type">Type:</label>
          <select id="type" v-model="process.type" 
            :style="{ backgroundColor: (process.type == 0) ? '#5E5E5E' :
                    (process.type == 1) ? '#00994D' :
                    (process.type == 2) ? '#5E55F3' :
                    (process.type == 3) ? '#CC3BCC' :
                    '#800007' }">
            <option value="0">Standard</option>
            <option value="1">Human</option>
            <option value="2">Code</option>
            <option value="3">External</option>
          </select>
        </div>
        <div class="variable-block">
          <label for="description">Description:</label>
          <textarea id="description" v-model="process.description"></textarea>
        </div>
        <div class="button-block">
          <button @click="updateOpenedAndSelectedProcessId" class="circle-button button-icon" data-tooltip="Open"><AkEnlarge /></button>
          <button @click="updateProcess" class="circle-button button-icon" data-tooltip="Apply"><AkCheckBox /></button>
          <button @click="deleteProcess" class="circle-button button-icon" data-tooltip="Delete"><FlFilledDeleteDismiss /></button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ContentService from '@/services/ContentService';
import NavigationService from '@/services/NavigationService';

//icons
import { AkEnlarge } from "@kalimahapps/vue-icons";
import { AkCheckBox } from "@kalimahapps/vue-icons";
import { FlFilledDeleteDismiss } from "@kalimahapps/vue-icons";


export default {
  components: {
    AkEnlarge,
    AkCheckBox,
    FlFilledDeleteDismiss
  },
  data() {
    return {
      process: {
        id: null,
        name: '',
        type: null,
        description: '',
        inputDescription: '',
        outputDescription: ''
      }
    };
  },
  created() {
    this.loadData();
  },
  methods: {
    async loadData() {
      const selectedProcessId = await NavigationService.getSelectedProcessId();
      this.process = await ContentService.getProcessInfo(selectedProcessId);

      if(this.process == null) {
        this.$emit("resetData");
      }
    },
    async updateProcess() {
      await ContentService.updateProcess(this.process);
      this.$emit("reloadData");
    },
    async deleteProcess() {
      if (this.process && this.process.id) {
        await ContentService.deleteProcess(this.process.id);
        this.$emit("reloadData");
      }
    },
    async updateOpenedAndSelectedProcessId() {
      if (this.process && this.process.id) {
        await NavigationService.updateOpenedProcessId(this.process.id);
        await NavigationService.updateSelectedProcessId(this.process.id);
        this.$emit("reloadData");
      }
    },
    closePopup() {
      this.$emit('close');
    }
  }
}
</script>

<style scoped>
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5); /* semi-transparent background */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1001;
}

.popup-content {
  background-color: #333333;
  border-radius: 10px;
  padding: 20px;
  min-width: 1000px; /* Minimum width */
  min-height: 500px; /* Minimum height */
  width: 50%; /* 50% width */
  height: 95%; /* 95% height */
  overflow: auto; /* Enable scrolling if content overflows */
  position: relative;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  cursor: pointer;
  font-size: 20px;
}

.process-info-popup {
  color: #dddddd;
  font-size: 14px;
}

.variable-block {
  margin-top: 20px;
}

.variable-block label {
  color: #5e5e5e;
}

.variable-block input,
.variable-block select,
.variable-block textarea {
  width: 100%;
  height: 30px;
  background-color: #333333;
  border: 1px solid #5e5e5e;
  border-radius: 5px;
  padding: 5px;
  color: #dddddd;
  font-size: 14px;
}

.variable-block textarea {
  height: 120px;
}

.button-block {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
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
  margin-bottom: 3px;
}

.circle-button::before {
  content: attr(data-tooltip); /* Use the data-tooltip attribute as the tooltip text */
  position: absolute;
  bottom: calc(100% + 5px); /* Center the tooltip vertically */
  left: 50%; /* Position the tooltip to the right of the button */
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
  transform: translateY(-5px) translateX(-50%); /* Move the tooltip to the right */
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
</style>
