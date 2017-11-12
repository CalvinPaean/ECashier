$(document).ready(function(){
	var trackTask;
    var preview = document.getElementById('preview');
    var faceObj = new tracking.ObjectTracker(['face']);
    var loading = document.querySelector(".loading");
    var stage = document.querySelector(".stage");
    var trackerCanvas = document.getElementById('facetracker');
    var resultSet = document.querySelector(".item-list ul");
    var items = {};
    var itemCodeReg = /\/(\d+)$/;
    var captureCanvas = document.getElementById('image_capture');
    var w = preview.clientWidth, h = preview.clientHeight;
    
    trackerCanvas.width = w;
    trackerCanvas.height = h;
    captureCanvas.width = w;
    captureCanvas.height = h;

    var trackerCtx = trackerCanvas.getContext('2d');
    var captureCtx = captureCanvas.getContext('2d');
    var initDelay = 3;
    var delayCtx = { timerId: -1, cnt : initDelay };
    var defaultCamera;

    var delayFunc = function(ctx) {
        return function() {
            if(!ctx || ctx.timerId === -1) return;
            if(ctx.cnt > 0)
                document.querySelector('.overlay').innerText = ctx.cnt;
            ctx.cnt -= 1;
            if(ctx.cnt < 0) {
                document.querySelector('.overlay').innerText = "";
                captureCtx.drawImage(preview, 0, 0, image_capture.width, image_capture.height);
                var dataUrl = captureCanvas.toDataURL('image/webp');
                if(trackTask) { 
                    trackerCtx.clearRect(0, 0, trackerCanvas.width, trackerCanvas.height);
                    trackTask.stop();
                }
                ctx.timerId = -1;
                ctx.cnt = initDelay;
                var scanner = new Instascan.Scanner({
                    video: preview,
                    scanPeriod: 1,
                    mirror : false
                });
                scanner.addListener('scan', function(content, image){

                    var code = content.match(itemCodeReg)[1];
                    if(code) {
                    	
                    	if(!items[code]) {
                        	$.get(`/item/${code}`, function(req){
                                var li = document.createElement("li");
                                li.classList.add("list-group-item");
                                li.setAttribute("data-code", code);
                                items[code] = 1;
                                var itemDiv = document.createElement("div");
                                itemDiv.classList.add("item");
                                li.append(itemDiv);
                                var text = document.createElement("span");
                                var quantity = document.createElement("span");
                                var wrapper = document.createElement("span");
                                wrapper.classList.add("item-quantity");
                                wrapper.innerText = "X";
                                quantity.innerText = "1";
                            	text.innerText = `${req.name}($${req.unitPrice})`;
                                itemDiv.append(text);
                                itemDiv.append(wrapper);
                                wrapper.append(quantity);
                                resultSet.append(li);
                        	});
                    	} else {
                    		var span = document.querySelector(`[data-code="${code}"] .item-quantity > span`);
                    		items[code] += 1;
                    		span.innerText = items[code];
                    	}
                    } else {
                    	alert("No Such Item!");
                    }
                    
                });
//                 request api to recoginize face
                stage.innerText = "Recoginizing...";
                captureCtx.drawImage(preview, 0, 0, w, h);
                var dataUrl = captureCanvas.toDataURL('image/webp'); 
                loading.classList.replace('hide','show');
                $.ajax({
                	url : '/auth/user/',
                	type : 'PUT',
                	data : dataUrl,
                	success : function(usr) {
                		if(usr.id) {
                    		stage.innerText = "QR Code Scanning";
                    		$('h4.card-title').text(usr.name);
                    		$('.card-text').text('Prime');
                            scanner.start(defaultCamera);
                		} else {
//                			alert('you are not in database');
                			stage.innerText = "Face Scanning";
                			ctx.timerId = -1;
                            ctx.cnt = initDelay;
                            trackTask.run();
                		}
                		loading.classList.replace('show','hide');
                	}
                });
                
            } else {
                ctx.timerId = setTimeout(delayFunc(ctx), 1000);
            }
        }
    }

    var onTrack = function(evt) {
        trackerCtx.clearRect(0, 0, trackerCanvas.width, trackerCanvas.height);
        if(evt.data.length == 0) {
            // not detected
            // if(delayCtx.timerId !== -1) {
            //     clearTimeout(delayCtx.timerId);
            //     delayCtx.timerId = -1;
            //     delayCtx.cnt = initDelay;    
            // }
        } else {

            if(delayCtx.timerId === -1) {
                delayCtx.timerId = 0;
                delayFunc(delayCtx)();
            }

            evt.data.forEach(function(rect) {
                trackerCtx.strokeStyle = 'blue';
                trackerCtx.lineWidth = 3;
                trackerCtx.strokeRect(rect.x, rect.y, rect.width, rect.height);
                // trackerCtx.font = '11px Helvetica';
                // trackerCtx.fillStyle = "#fff";
                // trackerCtx.fillText('x: ' + rect.x + 'px', rect.x + rect.width + 5, rect.y + 11);
                // trackerCtx.fillText('y: ' + rect.y + 'px', rect.x + rect.width + 5, rect.y + 22);
            });
        }
    }

    faceObj.setInitialScale(4);
    faceObj.setStepSize(2);
    faceObj.setEdgesDensity(0.1);
    faceObj.on('track', onTrack);
    trackTask = tracking.track(preview, faceObj);

    Instascan.Camera.getCameras().then(function(cameras){
        if(cameras.length > 0) {
            defaultCamera = cameras[0];
            defaultCamera.start().then(function(stream){
                preview.srcObject = stream;
            });
        }
    }).catch(function(err){
        console.error(err);
    });
});