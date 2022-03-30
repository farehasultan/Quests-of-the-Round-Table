
function subscriptions() {
    // subscribe to calculate stage winners
    stompClient.subscribe("/topic/calculateStage", function (response) {
      let data = JSON.parse(response.body);
      
      alert("Congratulations to: " + data);
    });
    
    //the response after setting the sponsor stages.
    stompClient.subscribe("/topic/setStages", function (response) { //need all players subscribe to this
      let data = JSON.parse(response.body); //should be an aray
  
      let stageSpecificDiv = document.createElement("div");
      let stageNumOfCardsDiv = document.createElement("div");
      let stages = data.stages;
      console.log("stages:" + stages);
      for (let i = 0; i < stages.length; i++) {
        stageSpecificDiv.append("Cards for Stages " + (i + 1));
        stageSpecificDiv.append(document.createElement("br"));
  
        stageNumOfCardsDiv.append("Cards for Stages " + (i + 1) + ": ");
        stageNumOfCardsDiv.append(stages[i].length + " cards");
        stageNumOfCardsDiv.append(document.createElement("br"));
        let hidden = document.createElement("div");
        hidden.setAttribute("id", "stage" + (i + 1));
        hidden.style.display = "none";
        stageNumOfCardsDiv.append(hidden);
        stageNumOfCardsDiv.append(document.createElement("br"));
  
        for (let j = 0; j < stages[i].length; j++) {
          stageSpecificDiv.append(stages[i][j]);
          stageSpecificDiv.append(document.createElement("br"));
  
          hidden.appendChild(document.createTextNode(stages[i][j]));
          hidden.appendChild(document.createElement("br"));
  
        }
      }
      if (data.sponsor == playerId) {
        document.getElementById("stages").appendChild(stageSpecificDiv);
      } else {
        stageSpecificDiv.style.display = "none";
        document.getElementById("stages").appendChild(stageNumOfCardsDiv);
      }
  
  
    });
    console.log("subscribed")
  
  
    const gameStartedSubscription = stompClient.subscribe('/topic/game/started', function (response) {
      let data = JSON.parse(response.body);
      if (response) game = response;
  
      gameId = game.gameID
  
      displayCreateGameResponse(data.body, playerName, parseInt(numOfPlayer));
  
      gameStartedSubscription.unsubscribe();
    });
  
    console.log("game started")
  
  
    const joinGameSubscription = stompClient.subscribe("/user/queue/joinGame", (response) => {
      const data = JSON.parse(response.body);
      playerId = data.body;
      showResponse(data, playerName);
  
      // setTimeout(() => { alert("Click on initialize cards to begin the game"); }, 2000);
      setTimeout(() => { stompClient.send("/app/ready", {}, ""); }, 2000);
      initializeAdv();
  
      joinGameSubscription.unsubscribe();
    })
  
    // subscribe to "wait for server to tell client to start"
    stompClient.subscribe("/topic/startTurn", (response) => { // does not get called
      console.log("This is after initilizing", response.body);
      if (response.body * 1 !== 0 && response.body * 1 === playerId) {
        alert("Pick a Story Card");
      }
    })
  
    //This function should also use session data and send it back.
    stompClient.subscribe("/topic/pickCard", function (response) {
      const data = JSON.parse(response.body);
      //console.log("From pick Card",data); //name: 'Slay the Dragon', drawer: null, storyCardType: 'Quest', totalStages: '3', foeName: 'Dragon', …}
      displayStoryCard(data);
    })
  
    //~~~~~~~~~~~~~~~~~~~~~~~~From finish Turn~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~/
  
  
    stompClient.subscribe("/topic/finishTurn", function (response) { //response = currentActiveplayer 
      let data = JSON.parse(response.body); //the id of the next active player..
      console.log(data);
  
      /**
       * {"currentActivePlayer":2,
       * "currentStoryCard":{"name":"Slay the Dragon","drawer":null,"storyCardType":"Quest","totalStages":"3","foeName":"Dragon","sponsor":1,"stages":[["Evil Knight","Saxons"],["Horse","Thieves"],["Mordred","Horse"]],"participantsId":[]},
       * "sponsorId":1,
       * "participantsId":[],
       * "questInPlay":false}
       * 
       * 
       */
      if (data.currentActivePlayer === playerId) {
        //activate their buttons
  
        // this needs work vv
        enableGameButtons();
        let currentStoryCard = data.currentStoryCard;
        if (currentStoryCard.storyCardType === "Quest") {
          //If the current story card type is quest, it could mean a few things
          //they're the sponsor, and it has looped back to them, the stage is complete
          if (currentStoryCard.sponsor === playerId && (currentStoryCard.currentStageNumber - 1) < (currentStoryCard.totalStages * 1)) {
            //check winner for data.currentstages
            //CLEAR THE HASHMAP FOR CLIENT STAGE
            //checkWinner(currentStoryCard.clientStages, currentStoryCard.stages); //this function does the functionality of sending the appropriate reward
            //and moving the turn
            // request for winner
            stompClient.send("/app/calculateStage"); //the response to this will be subscriptions so that everybody gets to see the dying player
            //after this nothing happens so we need the sponsor to click finish quest
            alert("player click finish quest!");
          }
          //they're the sponsor and this is the last and total stage
          if (currentStoryCard.sponsor === playerId && currentStoryCard.currentStageNumber === currentStoryCard.totalStages) {
            //check the winners again and reward them
            //for the sponsor, it should check how many total cards they used in all of the stages + total stages
            //for example, alert(pick 6 cards for sponsoring the quest);
            //send something to the server, stomp.client(/app/setStoryCardToNull );
          }
          //another scenario is that the player is not the sponsor.
          if (currentStoryCard.sponsor != playerId) {
            if (!currentStoryCard.participantsId.includes(playerId) && currentStoryCard.currentStageNumber === 1) {
            //ask them to join
                alert("click join quest");
            }
            if (currentStoryCard.participantsId.includes(playerId) && currentStoryCard.currentStageNumber <= currentStoryCard.totalStages) {
                //pick cards for this stage
                //they've already joined the quset, they have to pick cards for the next stage or withdraw
                alert("Pick cards for stage # " ,currentStoryCard.currentStageNumber);
          }
          if (!currentStoryCard.participantsId.includes(playerId) && currentStoryCard.currentStageNumber != 1) {
            //this player refused to join the quest;
            //finishTurn();//increment the currentActivePlayer and move to the next player
            alert("here 3")
             }
         }
        }
        if (currentStoryCard.storyCardType === "Event") {
            
    
        }
        if (currentStoryCard.storyCardType === "Tournament") {

    
        }
        if (currentStoryCard.storyCardType === null) {
            //if the story card type is numm it means they might be the first player
            alert("pick a story card");
        }
  
    }
    else if (data.currentActivePlayer != playerId) {
        //disable their buttons!
        //disableButtons();
    }
  
    });
}
  
  //~~~~~~~~~~~~~Subscription ends here~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~