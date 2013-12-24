<?php
require_once('../../scripts/sb_functions.php');

read_config();

header("Content-Type: text/css");
?>
	body {
		color: #<?php echo(get_user_color('txt_color')); ?>;
		background-color: #<?php echo(get_user_color('main_bg_color')); ?>;
		background-image: url('images/background-left.gif'), url('images/background-right.gif');
		background-position: top left, top right;
		background-repeat: repeat-y;
        	margin: 0px;
	        padding: 0px;
	        border: 0px;
	        font-family: Helvetica, Arial, Sans-Serif;
        	font-size: 12px;
        	font-weight: normal;
        	height: 100%;
	}

	p
	{
        	margin: 8px 0px 8px 0px;
	}

	img {
		border-style: none;
	}

	code, pre {
        	font-family: 'Courier New', Courier, Fixed;
	}
	
	pre {
		max-width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] ); ?>px;
        	overflow: auto;
        	border: 1px dotted #<?php echo(get_user_color('inner_border_color')); ?>; /* inner_border_color */
        	padding: 5px;
	}
	
	blockquote {
	        margin: 15px 30px 0 10px;
        	padding-left: 20px;
	        border-left: 5px solid #<?php echo(get_user_color('inner_border_color')); ?>;
	}

	h1, h2, h3, h4, h5, h6 {
		color: #<?php echo(get_user_color('headline_txt_color')); ?>;
        	font-family: 'Trebuchet MS', 'Gill Sans', Helvetica, sans-serif;
	        font-weight: bold;
	        margin: 2px 0px 2px 0px;
	}

	a:link, a:visited {
		color: #<?php echo(get_user_color('link_reg_color')); ?>;
	        font-weight: bold;
        	text-decoration: none;
	}

	a:hover {
		color: #<?php echo(get_user_color('link_hi_color')); ?>;
		text-decoration: underline;
	}

	a:active {
		color: #<?php echo(get_user_color('link_down_color')); ?>;
	}

form {
        font-size: 11px;
}

input, select, option, textarea
{
        font-size: 11px;
        text-align: left;
}


	hr	
	{
		color: #<?php echo(get_user_color('inner_border_color')); ?>;
		background-color: #<?php echo(get_user_color('inner_border_color')); ?>;
		margin: 4px 0px;
	}

	.copyright {
		color: #<?php echo(get_user_color('footer_txt_color')); ?>;
		font-size: 10px;
	}

	.subject {
		/* color: #<?php echo(get_user_color('headline_txt_color')); ?>; */
		color: #<?php echo(get_user_color('entry_title_text')); ?>;
        	font-family: 'Trebuchet MS', 'Gill Sans', Helvetica, sans-serif;
        	font-size: 18px;
        	font-weight: bold;
	        padding: 4px 0px;
	}
	

	.byline {
		color: #<?php echo(get_user_color('date_txt_color')); ?>;
        	font-size: 10px;
	        padding: 2px 0px;
	}
	
	#header {
		color: #<?php echo(get_user_color('header_txt_color')); ?>;
		background-color: #<?php echo(get_user_color('header_bg_color')); ?>;
	        min-height: 25px;
        	background: url(images/nav-background.gif) repeat-x;
	}

	#header a, #header a:hover
	{
	        color: inherit;
		font-size: inherit;
	        font-weight: inherit;
        	text-decoration: inherit;
	}
	
	#footer {
		background-color: #<?php echo(get_user_color('footer_bg_color')); ?>;
	        padding: 4px 20px;
        	clear: both;
	        border-top: #999999 1px solid;
	}
	
	#sidebar a:link, #sidebar a:visited
	{
		color: #<?php echo(get_user_color('menu_link_reg_color')); ?>;
	}

	#page
	{
		margin-left: 100px; margin-right: 100px;
	}
	
	#sidebar a:hover
	{
		color: #<?php echo(get_user_color('menu_link_hi_color')); ?>;
	}
	
	#sidebar a:active
	{
		color: #<?php echo(get_user_color('menu_link_down_color')); ?>;
	}
	
	#sidebar .menu_title,  #sidebar .menu_title a:link, #sidebar .menu_title a:visited, #sidebar .menu_title a:hover, #sidebar .menu_title a:active {
		color: #<?php echo(get_user_color('menu_title_text')); ?>;
		background-color: #<?php echo(get_user_color('menu_title_bg')); ?>;
		border-color: #<?php echo(get_user_color('menu_border')); ?>;
	        font-family: 'Trebuchet MS', 'Gill Sans', Helvetica, sans-serif;
        	font-weight: bold;
	        padding: 7px 5px 5px 6px;
	}
	
#sidebar .menu_title a, #sidebar .menu_title:hover
{
        text-decoration: none;
        color: inherit;
}

	
	#sidebar .menu_body {
		color: #<?php echo(get_user_color('menu_text')); ?>;
		background-color: #<?php echo(get_user_color('menu_bg')); ?>;
	        padding: 10px;
        	margin-bottom: 10px;
	        border: #<?php echo(get_user_color('menu_border')); ?> 1px solid;
	}

  #sidebar {
        max-width: <?php echo $theme_vars[ 'menu_width' ] ?>px;
        padding: 10px;
        float: right;
	clear: both;
  }

#maincontent
{
        padding: 20px 20px 20px 20px;
}

#popup
{
	padding: 20px;
}

.comment
{
	margin-bottom: 12px;
	/* border: 1px #0f0 dashed; */
}

.entry
{
	margin-bottom: 24px;
}

.comment .subject
{
	font-size: 16px;
}

.content
{
	padding: 10px 0px;
}

.buttons
{
	padding: 4px 0px;
}

/* Sidebar */

#sidebar .calendar a
{
	font-weight: bold;
	text-decoration: none;
}

#archive_tree_menu li	
{
	margin: 0px;
	padding: 0px;
	/* border: 1px #F0F dashed; */
}
	
	/* entry_bg
	entry_title_bg
	entry_border
	entry_title_text
	entry_text */

                div #toggleSetupLanguage, #toggleSetupGeneral, #toggleSetupEntries, #toggleSetupSidebar, #toggleSetupTrackbacks, #toggleSetupComments, #toggleSetupCompression
                {
                        border-color: #<?php echo(get_user_color('inner_border_color')); ?>;
                }

