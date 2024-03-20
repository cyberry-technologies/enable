<template>
  <div class="info-menu">
    <div class="variable-block">
      <label for="name">Name:</label>
      <input type="text" id="name" v-model="process.name" readonly>
    </div>
    <div class="variable-block">
      <label for="type">Type:</label>
      <p v-if="process.type == 0" type="text" id="type" :style="{ backgroundColor: '#5E5E5E' }">Standard</p>
      <p v-if="process.type == 1" type="text" id="type" :style="{ backgroundColor: '#00994D' }">Human</p>
      <p v-if="process.type == 2" type="text" id="type" :style="{ backgroundColor: '#5E55F3' }">Code</p>
      <p v-if="process.type == 3" type="text" id="type" :style="{ backgroundColor: '#CC3BCC' }">External</p>
    </div>
    <div class="variable-block">
      <label for="description">Description:</label>
      <textarea id="description" v-model="process.description" readonly></textarea>
    </div>
    <div class="button-block">
      <button @click="updateOpenedAndSelectedProcessId" class="circle-button button-icon" data-tooltip="Open"><AkEnlarge /></button>
      <button @click="openProcessInfoPopup" class="circle-button button-icon" data-tooltip="Edit"><AnFilledEdit /></button>
    </div>
  </div>
</template>

<script>
import ContentService from '@/services/ContentService';
import NavigationService from '@/services/NavigationService';

//icons
import { AkEnlarge } from "@kalimahapps/vue-icons";
import { AnFilledEdit } from "@kalimahapps/vue-icons";

export default {
  components: {
    AkEnlarge,
    AnFilledEdit
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
    openProcessInfoPopup() {
      this.$emit("openProcessInfoPopup");
    },
    async updateOpenedAndSelectedProcessId() {
      await NavigationService.updateOpenedProcessId(this.process.id);
      await NavigationService.updateSelectedProcessId(this.process.id);

      this.$emit("reloadData");
    },
  }
}
</script>

<style scoped>
  .info-menu {
    position: fixed;
    top: 0;
    right: 0;
    width: 20%;
    height: 96%;
    margin: 1%;
    background-color: #333333;
    border-radius: 20px;
    padding: 10px;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    color: #dddddd;
    font-size: 10px;
  }

  .variable-block {
    margin-top: 20px;
  }

  .variable-block label {
    color: #5e5e5e;
  }

  .variable-block input,
  .variable-block select,
  .variable-block p,
  .variable-block textarea {
    width: 100%;
    height: 30px;
    background-color: #333333;
    border: 1px solid #5e5e5e;
    border-radius: 5px;
    padding: 5px;
    color: #dddddd;
    font-size: 11px;
  }

  .variable-block textarea {
    height: 50%;
  }

  .button-block {
    display: flex;
    justify-content: space-around;
    margin-top: auto;
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

