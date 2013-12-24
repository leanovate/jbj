<?php
require_once('../../scripts/sb_functions.php');

read_config();

    $content_width = $theme_vars[ 'content_width' ];
    $menu_width = $theme_vars[ 'menu_width' ];
    $page_width = $content_width + $menu_width;

header("Content-Type: text/css");
?>
	body {
		background-color: #<?php echo(get_user_color('bg_color')); ?>;
		color: #<?php echo(get_user_color('txt_color')); ?>;
  font-size: 14px;
  margin: 0px;
  padding: 0px;
  font-family: Georgia, serif;
  letter-spacing: 1.5px;
	}

	hr	
	{
		color: #<?php echo(get_user_color('inner_border_color')); ?>;
		background-color: #<?php echo(get_user_color('inner_border_color')); ?>;
	}

	img {
		border-style: none;
	}

	h1, h2, h3, h4, h5, h6 {
		color: #<?php echo(get_user_color('headline_txt_color')); ?>;
		font-family: 'Trebuchet MS', 'Gill Sans', Helvetica, sans-serif;
		font-weight: bold;
		margin: 2px 0px 2px 0px;

	}

	a {
		font-weight: normal;
		text-decoration: none;
	}
	
	a:link, a:visited {
		color: #<?php echo(get_user_color('link_reg_color')); ?>;
	}
	
	a:hover {
		color: #<?php echo(get_user_color('link_hi_color')); ?>;
	}
	
	a:active {
		color: #<?php echo(get_user_color('link_down_color')); ?>;
	}

code, pre {
  font-family: 'Courier New', Courier, Fixed;
}

pre {
  overflow: auto;
  max-width: 430px;
  border: 1px dotted #777;
  padding: 5px
}

input, select, option, textarea
{
  font-size: 11px;
  text-align: left;
}

blockquote {
  margin: 15px 30px 0 45px;
  padding: 0 0 0 45px;
  background: url('images/blockquote.gif') no-repeat left top;
  font-style:italic;
}


	#header {
		border-color: #<?php echo(get_user_color('border_color')); ?>;
		color: #<?php echo(get_user_color('header_txt_color')); ?>;
		background-color: #<?php echo(get_user_color('header_bg_color')); ?>;
		margin: 0;
		background-position: top;
		height: 41px;
		position: relative;
		left: 0px;
		top: 0px;
	}

#HeaderLeft {
  background-image: url('images/logo_area_side.gif');
  background-repeat: no-repeat;
  background-position: top;
  height: 41px;
  width: 1px;
  position: absolute;
  left: 0px;
}

#HeaderRight {
  background-image:  url('images/logo_area_side.gif');
  background-repeat: no-repeat;
  background-position: top;
  height: 41px;
  width: 1px;
  position: absolute;
  right: 0px;
}

#HeaderCenter {
  background-image: url('images/logo_area.gif');
  background-repeat: repeat-x;
  height: 41px;
  width: 100%;
  position: absolute;
  left: 1px;
  padding: 20px 20px;
  font-family: Georgia, "Times New Roman", Times, serif;
  font-size: 20px;
  text-decoration: none;
}

	
	#footer {
		color: #<?php echo(get_user_color('footer_txt_color')); ?>;
		background: #<?php echo(get_user_color('footer_bg_color')); ?>;
		border-top: 1px solid #<?php echo(get_user_color('border_color')); ?>;
		width: 100%;
		background-color: #<?php echo(get_user_color('footer_bg_color')); ?>;
		border: 0;
		padding: 10px;
		text-align: left;
		clear: both;
	}
	
	
	#maincontent .blog_title {
		border-color: #<?php echo(get_user_color('entry_border')); ?>;
		color: #<?php echo(get_user_color('entry_title_text')); ?>;
		background-color: #<?php echo(get_user_color('entry_bg')); ?>;
		font-size: 20px;
		padding: 0px 6px 0px 0px;
		border-bottom-width: 1px;
		border-bottom-style: dashed;
	}

	#maincontent .blog_title img
	{
		/* padding: top right bottom left */
		padding: 0px 10px 0px 0px;
	}
	
	#maincontent .blog_body {
		border-color: #<?php echo(get_user_color('entry_border')); ?>;
		color: #<?php echo(get_user_color('entry_text')); ?>;
		background-color: #<?php echo(get_user_color('entry_bg')); ?>;
		padding: 0px; /* Used to be 10px */
		margin: 0px;
		line-height: 16px; /* This is new */
		border-width: 1px;
		border-style: none;
	}

#maincontent .blog_body_clear
{
  padding: 0px;
  border-color: #FFF;
  border-width: 0px;
  border-style: solid;
}

#maincontent .blog_body_solid
{
  padding: 20px 0px 0px 0px;
  border-color: #660;
  background: #fff;
  border-width: 0px;
  border-style: solid;
}

	
	#maincontent .blog_comment {
		border-color: #<?php echo(get_user_color('entry_border')); ?>;
		color: #<?php echo(get_user_color('entry_text')); ?>;
		background-color: #<?php echo(get_user_color('entry_bg')); ?>;
		padding: 6px 10px 0px 0px;
		font-size: 10px;
		font-weight: bold;
	}
	
	#maincontent .blog_date {
		color: #<?php echo(get_user_color('date_txt_color')); ?>;
		font-weight: normal;
		font-size: 10px;
		margin-bottom: 4px;
		top: 0px;
	}

	#pagebody {
/*		margin-left:auto; margin-right:auto; */
/*		width: 100%;*/
		border: 1px solid #<?php echo(get_user_color('border_color')); ?>;
		background-color: #<?php echo(get_user_color('header_bg_color')); ?>;
	}

	#maincontent {
		background-color: #<?php echo(get_user_color('main_bg_color')); ?>;
		/*width: <?php echo $theme_vars[ 'content_width' ] ?>px;*/
		/*margin-left: <?php echo $theme_vars[ 'menu_width' ] ?>px;*/
		margin-left: auto;
		padding: 12px;
		top: 0px;
	}

	#sidebar {
		width: <?php echo $theme_vars[ 'menu_width' ] ?>px;
		background-color: #<?php echo(get_user_color('menu_bg_color')); ?>;
		border-left: 1px solid #<?php echo(get_user_color('inner_border_color')); ?>;
		background-image: url('images/menu_bg.gif');
/*		height: 100%; */
		padding: 0px 0px 0px 12px;
		font-size: 11px;
		font-weight: normal;
		float: left;
	}
	
	#sidebar .menu_title {
		border-color: #<?php echo(get_user_color('menu_border')); ?>;
		color: #<?php echo(get_user_color('menu_title_text')); ?>;
		font-family: 'Trebuchet MS', 'Gill Sans', Helvetica, sans-serif;
		font-size: 11px;
		font-weight: bold;
		padding: 0px 5px 3px 7px;
		margin: 0px;
		/*background-color: #<?php echo(get_user_color('menu_title_bg')); ?>;*/
	}
	
	#sidebar .menu_body {
		border-color: #<?php echo(get_user_color('menu_border')); ?>;
		color: #<?php echo(get_user_color('menu_text')); ?>;
		/*background-color: #<?php echo(get_user_color('menu_bg')); ?>;*/
		padding: 5px;
		clear: both;
		margin: 0px;
	}

	#sidebar .menu_body a {
		font-weight: normal;
		text-decoration: none;
	}

	#sidebar .menu_body a:link {
		color: #<?php echo(get_user_color('menu_link_reg_color')); ?>;
	}
	
	#sidebar .menu_body a:visited {
		color: #<?php echo(get_user_color('menu_link_reg_color')); ?>;
	}
	
	#sidebar .menu_body a:hover {
		color: #<?php echo(get_user_color('menu_link_hi_color')); ?>;
	}
	
	#sidebar .menu_body a:active {
		color: #<?php echo(get_user_color('menu_link_down_color')); ?>;
	}

/* NEW */
#archive_tree_menu li
{
  margin: 0px;
  padding: 4px 0px 0px 0px;
  /* border: 1px #F00 solid; */
}

#sidebar .calendar a
{
font-size: 11px;
font-weight: bold;
text-decoration: none;
}

                div #toggleSetupLanguage, #toggleSetupGeneral, #toggleSetupEntries, #toggleSetupSidebar, #toggleSetupTrackbacks, #toggleSetupComments, #toggleSetupCompression
                {
                        border-color: #<?php echo(get_user_color('inner_border_color')); ?>;
                }

