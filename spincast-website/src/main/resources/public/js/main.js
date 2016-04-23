
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
    editor.setHighlightActiveLine(false);
    editor.setDisplayIndentGuides(false);
    editor.setTheme("ace/theme/eclipse");
    editor.setReadOnly(true);
    editor.setAutoScrollEditorIntoView(true);
    editor.setOption("minLines", 2);
    editor.setOption("maxLines", 2000);  
    editor.setFontSize(14);
    editor.setShowPrintMargin(false);
    editor.renderer.setScrollMargin(0, 4, 0, 0);
    
    if($(acePre).hasClass("ace-xml")) {
        editor.getSession().setMode("ace/mode/xml");
    } else {
        editor.getSession().setMode("ace/mode/java");
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
        
        var pipeTokens = rangeStr.split("|");
        for(var i = 0; i < pipeTokens.length; i++) {
            var pipeToken = pipeTokens[i];
            var tokens = pipeToken.split(","); 
            if(tokens && tokens.length == 4) {
                var marker = editor.session.addMarker(new app.ace.Range(tokens[0], tokens[1], tokens[2], tokens[3]),'ace_active-line', 'text');
            }
        }
    }
}

app.aceInit = function() {
	
    //Init Ace editors when the <pre> are visible
    $('.ace').bind('inview', function (event, visible) {
        if(visible == true) {
        	if($(this).hasClass("ace_editor")) {
        		return;
        	}
        	app.aceEditor(this);
        }
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
    app.affix('#mobileMenu', trigger);
}

app.affix = function(selector, trigger) {
    $(selector).affix({
        offset: {
          top: trigger
        }
    });
}

/******************************************
 * On document ready...
 ******************************************/
$(function() {
    app.initHiLine();
    app.spincastHover();
    app.aceInit();

});






