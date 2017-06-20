
var app = app || {};

/**
 * Highligh one or more lines of code when a page number or
 * page range is hovered (ex : page "3" or "3-6").
 * When the page number is clicked, the highlithing will stay
 * on, even on mouse out.
 */
app.initHiLine = function() {
    
    function getLinesFromToToHi(nbrSpan) {
        var lines = [];
        var val = $(nbrSpan).text();
        if(val.indexOf("-") < 0) {
            lines.push(val);
            lines.push(val);
        } else {
            var limits = val.split("-");
            if(limits.length != 2) {
                return;
            }
            lines.push(limits[0]); 
            lines.push(limits[1])
        } 
        
        return lines;
    }
	
	$(".code-line-nbr").hover(
	        
	    function(){
	        var linesFromToToHi = getLinesFromToToHi(this);
	        
	        var acePre = $(this).closest("div").find("pre.ace");
	        var editor = ace.edit(acePre[0]);

	        var markerName = linesFromToToHi[0] + "-" + linesFromToToHi[1];
	        var markerId = editor.markerIds[markerName];
	        if(markerId) {
	        	editor.session.removeMarker(markerId);
	        	delete editor.markerIds[markerName];
	        }
	        
	        var markerId = editor.session.addMarker(new app.ace.Range(linesFromToToHi[0] -1, 0, linesFromToToHi[1] - 1, 1000), "ace_active-line", "fullLine");
	        editor.markerIds[markerName] = markerId;  
	    },
	    
	    function(){
	    	
	        if($(this).hasClass("stayHi")) {
	           return; 
	        }
	        
	        var linesFromToToHi = getLinesFromToToHi(this);
	        
	        var acePre = $(this).closest("div").find("pre.ace");
	        var editor = ace.edit(acePre[0]);

	        var markerName = linesFromToToHi[0] + "-" + linesFromToToHi[1];
	        var markerId = editor.markerIds[markerName];
	        if(markerId) {
	        	editor.session.removeMarker(markerId);
	        	delete editor.markerIds[markerName];
	        }
	    }  
	);
	
	$(".code-line-nbr").click(function() {
        if($(this).hasClass("stayHi")) {
            $(this).removeClass("stayHi"); 
        } else {
            $(this).addClass("stayHi"); 
        }
	});
}

/******************************************
 * The spinning effect uses :
 * http://www.jqueryscript.net/animation/jQuery-CSS3-Based-Text-Animation-Effect-Plugin-LetterFX.html
 ******************************************/
app.spincastHover = function() {
    $(".spincast").mouseover(function() {
        $(this).letterfx({fx:'spin', fx_duration:"3s"});  
    });
};
app.ace = {};
app.ace.Range = ace.require('ace/range').Range;

app.aceEditor = function(acePre) {
    var editor = ace.edit(acePre);
    editor.$blockScrolling = Infinity;
    editor.setHighlightActiveLine(false);
    editor.setDisplayIndentGuides(false);

    editor.setReadOnly(true);
    editor.setAutoScrollEditorIntoView(true);
    editor.setOption("minLines", 2);
    editor.setOption("maxLines", 2000);  
    editor.setFontSize(14);
    editor.setShowPrintMargin(false);
    editor.renderer.setScrollMargin(0, 4, 0, 0);
    editor.getSession().setUseWorker(false);
    
    // Loads the mode and the theme
    var aceMode = $(acePre).attr('class').match(/\bace-([-_a-zA-Z]+)\b/)[1];
    if(aceMode) {
    	editor.getSession().setMode("ace/mode/" + aceMode);
    	if(aceMode === "java") {
    		editor.setTheme("ace/theme/eclipse");
    	} else {
    		editor.setTheme("ace/theme/idle_fingers");
    	}  
    } else {
        editor.setTheme("ace/theme/idle_fingers");
        editor.getSession().setMode("ace/mode/text");
    }

    // Used to store the markers we manage.
    editor.markerIds = {};
    
    // Doesn't show the cursor on the first display.
    editor.renderer.$cursorLayer.element.style.display = "none"
    $(acePre).click(function() {
    	editor.renderer.$cursorLayer.element.style.display = "inline"
    });
    
    // This prevents the matching end XML tag to be "selected"
    // on the first display.
    editor.moveCursorTo(0,1);

    // Default highlighting?
    var rangeStr = $(acePre).attr("data-ace-hi");
    if(rangeStr) {

    	let markersNbr = 0;
        var pipeTokens = rangeStr.split("|");
        for(var i = 0; i < pipeTokens.length; i++) {
            var pipeToken = pipeTokens[i];
            var tokens = pipeToken.split(","); 
            if(tokens && tokens.length == 4) {
                var marker = editor.session.addMarker(new app.ace.Range(tokens[0], tokens[1], tokens[2], tokens[3]),'ace_active-line', 'text'); 
                markersNbr++;
            }
        }
    }
    
    // Default error highlighting?
    rangeStr = $(acePre).attr("data-ace-error");
    if(rangeStr) {

        var pipeTokens = rangeStr.split("|");
        for(var i = 0; i < pipeTokens.length; i++) {
            var pipeToken = pipeTokens[i];
            var tokens = pipeToken.split(","); 
            if(tokens && tokens.length == 4) {
            	var marker = editor.session.addMarker(new app.ace.Range(tokens[0], tokens[1], tokens[2], tokens[3]),'ace_active-line ace-error', 'text'); 
            	editor.session.addGutterDecoration(tokens[0], 'ace-error-gutter');
            }
        }
    }
    
}

app.aceInit = function() {
	
    // Init Ace editors when the <pre> are visible
    $('.ace').bind('inview', function (event, visible) {
        if(visible == true) {
        	if($(this).hasClass("ace_editor")) {
        		return;
        	}
        	app.aceEditor(this);
        }
    });
    
    $('.ace_active-line').bind('inview', function (event, visible) {
        if(visible == true) {
        	$(this).css("width", 300);
        }
    });
    
    // Ajust the Ace inline highlights, when they are created...
    $(".ace").arrive(".ace_active-line", function() {
        var hi = $(this);
        
        let width = hi.width();
        let height = hi.height();
        let position = hi.position();
        let left = position.left;
        let top = position.top;
        
        hi.css("width", width + 14);
        hi.css("height", height + 6);  
        hi.css("left", left - 6);  
        hi.css("top", top - 1); 
    });
    
    // Trigger a first check for Ace editors and
    // relocates the page at the correct hash position or otherwise
    // the ace editors make the page being too low.
    $(window).scroll();
    if(window.location.hash) {
        window.location = window.location;
    }
}

/******************************************
 * Initialize a Table Of Content.
 * Based on http://www.codingeverything.com/2014/02/BootstrapDocsSideBar.html
 * 
 * @param trigger Is the offset at which the TOC will become fixed.
 * @param changeSectionOffset Is the offset from the top to make a section
 *        of the TOC change, when scrolling.
 ******************************************/
app.toc = function(trigger, changeSectionOffset) {
    
    changeSectionOffset = (typeof changeSectionOffset == "undefined") ? 100 : changeSectionOffset;
	
    $('body').scrollspy({
        target: '#toc',
        offset: changeSectionOffset
    });
    $('#toc > ul').affix({
        offset: {
          top: trigger
        }
    });
    
    /******************************************
     * Fix for the sidebar width
     ******************************************/
    $('#toc ul.affix').width($('#toc').width());
    $(window).scroll(function () {
        $('#toc ul.affix').width($('#toc').width());
    });	
    
    app.affix('#sectionTitleFixed', trigger);
    app.affix('#tocTopUl', trigger);
    app.affix('#mobileMenu', trigger);
}

app.affix = function(selector, trigger) {
    $(selector).affix({
        offset: {
          top: trigger
        }
    });
}

app.alertMessage = function(alertType, alertText) {
	
	toastr.options = {
			  "closeButton": true,
			  "debug": false,
			  "newestOnTop": false,
			  "progressBar": true,
			  "positionClass": "toast-top-full-width",
			  "preventDuplicates": false,
			  "onclick": null,
			  "showDuration": "300",
			  "hideDuration": "1000",
			  "timeOut": "7000",
			  "extendedTimeOut": "1000",
			  "showEasing": "swing",
			  "hideEasing": "linear",
			  "showMethod": "fadeIn",
			  "hideMethod": "fadeOut" 
			}
	
	if(alertType === "SUCCESS") {
		toastr.success(alertText);
	} else if(alertType === "WARNING") {
		toastr.warning(alertText);
	} else {
		toastr.error(alertText);
	}
}

/******************************************
 * On document ready...
 ******************************************/
$(function() {
    app.initHiLine();
    app.spincastHover();
    app.aceInit();
});






