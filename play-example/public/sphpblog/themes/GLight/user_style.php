<?php
require_once('../../scripts/sb_functions.php');

read_config();

   $content_width = $theme_vars[ 'content_width' ];
    $menu_width = $theme_vars[ 'menu_width' ];
    $page_width = $content_width + $menu_width;

    $header_graphic = $blog_config->getTag('BLOG_HEADER_GRAPHIC');
    if ( $header_graphic == '' ) {
       $header_graphic = 'images/header750x100.jpg';
    } else {
       $header_graphic = '../../' . $header_graphic;
    }

header("Content-Type: text/css");
?>
  body {
    color: #<?php echo(get_user_color('txt_color')); ?>;
    font-size: 11px;
    margin: 0px;
    padding: 0px;
    background-color: #FFFFFF; /* bg_color */

    /* Standard. Readable */
    font-family: Arial, Helvetica, Sans-Serif;
  }

p
{
  margin: 8px 0px 8px 0px;
}

  h1, h2, h3, h4, h5, h6
  {
    font-family: Arial, Helvetica, Sans-Serif;
    font-weight: bold;
    /* text-shadow: #bbb 2px 2px 1px; */
    margin: 2px 0px 2px 0px;
    color: #<?php echo(get_user_color('headline_txt_color')); ?>;
  }

  hr
  {
    color: #<?php echo(get_user_color('inner_border_color')); ?>;
    background-color: #<?php echo(get_user_color('inner_border_color')); ?>;
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
    border: 1px dotted #<?php echo(get_user_color('inner_border_color')); ?>;
    padding: 5px;
  }

  blockquote {
    color: #777;
    margin: 15px 30px 0 10px;
    padding-left: 20px;
    border-left: 5px solid #ddd;
  }

  a:link, a:visited {
    color: #<?php echo(get_user_color('link_reg_color')); ?>;
    font-weight: normal;
    text-decoration: none;
  }

  a:hover {
    color: #<?php echo(get_user_color('link_hi_color')); ?>;
    text-decoration: none;
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


  #page {
    margin-left:auto; margin-right:auto;
    border: 1px solid #<?php echo(get_user_color('border_color')); ?>;
    max-width: <?php echo( $page_width ); ?>px;
  }

  #pagebody {
/*    margin-left:auto; margin-right:auto; */
  }

  #header {
    min-height: 15px;
    padding: 85px 5px 5px 10px;
    background-repeat: no-repeat;
    background-image: url('<?php echo( $header_graphic ); ?>');
    margin-left:auto; margin-right:auto;
    max-width: <?php echo( $page_width ); ?>px;
    background-color: #<?php echo(get_user_color('header_bg_color')); ?>;
    border-color: #<?php echo(get_user_color('border_color')); ?>;
    color: #<?php echo(get_user_color('headline_txt_color')); ?>;
    border-width: 0px 0px 0px 0px;
    border-style: solid;
    font-family: Arial, Helvetica, Sans-Serif;
    font-size: 14px;
    font-weight: bold;
  }

  #footer {
    color: #<?php echo(get_user_color('footer_txt_color')); ?>;
    background: #<?php echo(get_user_color('footer_bg_color')); ?>;
    border-top: 1px solid #<?php echo(get_user_color('border_color')); ?>;
    width: <?php echo( $page_width ); ?>px;
    background-color: #<?php echo(get_user_color('footer_bg_color')); ?>;
    border: 0;
    padding: 10px;
    text-align: left;
    clear: both;
  }

  #maincontent .blog_subject {
    color: #<?php echo(get_user_color('entry_title_text')); ?>;
    border-color: #<?php echo(get_user_color('entry_border')); ?>;
    background-color: #<?php echo(get_user_color('entry_border')); ?>;
    font-family: Arial, Helvetica, Sans-Serif;
    font-size: 14px;
    font-weight: bold;
    margin: 0px;
    border-width: 7px 18px 0px 18px;
    border-style: solid;
  }

  #maincontent .blog_subject img
  {
    /* padding: top right bottom left */
    padding: 0px 10px 0px 0px;
  }

  #maincontent .blog_date {
    color: #<?php echo(get_user_color('date_txt_color')); ?>;
    font-weight: normal;
    font-size: 10px;
    margin-bottom: 5px;
    margin-top: -7px;
  }

  #maincontent .blog_categories {
    color: #<?php echo(get_user_color('date_txt_color')); ?>;
  }

  #maincontent .blog_body {
    border-color: #<?php echo(get_user_color('entry_border')); ?>;
    color: #<?php echo(get_user_color('entry_text')); ?>;
    padding: 12px; /* Used to be 10px */
    margin: 0px;
    line-height: 14px; /* This is new */
    background-color: #FFFFFF;
    border-width: 7px 7px 0px 7px;
    border-style: solid;
    background: #fff url(images/under_entry_title.png) repeat-x 0 0;
  }

  #maincontent .blog_body_clear {
    padding: 0px;
    border-color: #FFF;
    border-width: 0px;
    border-style: solid;
  }

  #maincontent .blog_body_solid  {
    border-color: #<?php echo(get_user_color('entry_border')); ?>;
    color: #<?php echo(get_user_color('entry_text')); ?>;
    background-color: #<?php echo(get_user_color('entry_border')); ?>;
    padding: 1px;
    background: #fff;
    border-width: 5px;
    border-style: solid;
  }

  #maincontent .entry_top {
    border-color: #<?php echo(get_user_color('entry_border')); ?>;
    background-color: #<?php echo(get_user_color('entry_border')); ?>;
    margin: 0;
    padding: 0px;  /* 10px; */
    border: 0px;
    border-width: 0px 0px 0px 0px;
    height: 0px;
    empty-cells: show;
  }

  #maincontent .entry_bottom {
    border-color: #<?php echo(get_user_color('entry_border')); ?>;
    background-color: #<?php echo(get_user_color('entry_border')); ?>;
    margin: 0;
    padding: 0px;  /* 10px; */
    border: 0px;
    border-width: 0px 0px 0px 0px;
    height: 2px;
    empty-cells: show;
  }

  #maincontent .blog_byline
  {
    color: #999999; /* date_txt_color */
    font-size: 9px;
    margin-bottom: 10px;
  }

  #maincontent .blog_comment {
    background-color: #<?php echo(get_user_color('entry_title_bg')); ?>;
    border-color: #<?php echo(get_user_color('entry_border')); ?>;
    padding: 6px 10px 6px 10px;
    color: #663;
    border-width: 0px 7px 7px 7px;
    border-style: solid;
  }

  /* Comment differences */

  #maincontent .blog_subject_comment {
    color: #<?php echo(get_user_color('menu_title_text')); ?>;
    border-color: #<?php echo(get_user_color('menu_border')); ?>;
    background-color: #<?php echo(get_user_color('menu_border')); ?>;
    font-family: Arial, Helvetica, Sans-Serif;
    font-size: 14px;
    font-weight: bold;
    margin: 0px;
    border-width: 7px 18px 0px 18px;
    border-style: solid;
  }

  #maincontent .entry_top_comment {
    border-color: #<?php echo(get_user_color('menu_border')); ?>;
    background-color: #<?php echo(get_user_color('menu_border')); ?>;
    margin: 0;
    padding: 0px;  /* 10px; */
    border: 0px;
    border-width: 0px 0px 0px 0px;
    height: 0px;
    empty-cells: show;
  }

  #maincontent .entry_bottom_comment {
    border-color: #<?php echo(get_user_color('menu_border')); ?>;
    background-color: #<?php echo(get_user_color('menu_border')); ?>;
    margin: 0px;
    padding: 0px;  /* 10px; */
    border: 0px;
    border-width: 0px 0px 0px 0px;
    height: 2px;
    empty-cells: show;
  }

  #maincontent .blog_body_comment {
    border-color: #<?php echo(get_user_color('menu_border')); ?>;
    color: #<?php echo(get_user_color('entry_text')); ?>;
    padding: 12px; /* Used to be 10px */
    margin: 0px;
    line-height: 14px; /* This is new */
    background-color: #FFFFFF;
    border-width: 7px 7px 0px 7px;
    border-style: solid;
    background: #fff url(images/under_entry_title.png) repeat-x 0 0;
  }

  #maincontent .blog_comment_comment {
    background-color: #<?php echo(get_user_color('menu_bg')); ?>;
    border-color: #<?php echo(get_user_color('menu_border')); ?>;
    padding: 6px 10px 6px 10px;
    color: #663;
    border-width: 0px 7px 7px 7px;
    border-style: solid;
  }

  #maincontent {
    max-width: <?php echo $theme_vars[ 'content_width' ] -20 ?>px;
    background-color: #<?php echo(get_user_color('main_bg_color')); ?>;
    margin-<?php echo $theme_vars[ 'menu_align' ]; ?>: auto;
    padding: 10px;
    border: 5px;
  }

  /* Side bar */

  #sidebar .menu_title {
    background-color: #<?php echo(get_user_color('menu_border')); ?>;
    color: #<?php echo(get_user_color('menu_title_text')); ?>;
    border-color: #<?php echo(get_user_color('menu_border')); ?>;
    font-family: 'Trebuchet MS', 'Gill Sans', Helvetica, sans-serif;
    font-weight: bold;
    border-style: solid;
    border-width: 2px 2px 2px 2px;
    padding: 3px 3px 1px 3px;
  }

  #sidebar .menu_title a
  {
    text-decoration: none;
    color: inherit;
    font-weight: bold;
  }

  #sidebar .menu_body {
    border-color: #<?php echo(get_user_color('menu_border')); ?>;
    background-color: #<?php echo(get_user_color('menu_bg')); ?>;
    padding: 10px;
    border-width: 3px;
    border-style: solid;
  }

  #sidebar {
    max-width: <?php echo $theme_vars[ 'menu_width' ]-10 ?>px;
    background-color: #<?php echo(get_user_color('menu_bg_color')); ?>;
    float: <?php echo $theme_vars[ 'menu_align' ]; ?>; 
    padding: 10px;
  }

#sidebar .divider {
  margin: 8px 0px 8px 0px;
}

#sidebar .calendar a
{
  font-weight: bold;
  text-decoration: none;
}

/* NEW */
#archive_tree_menu li
{
  margin: 0px;
  padding: 0px;
  /* border: 1px #F0F dashed; */
}

.divider { margin: 20px 0px 15px 0px; }

                div #toggleSetupLanguage, #toggleSetupGeneral, #toggleSetupEntries, #toggleSetupSidebar, #toggleSetupTrackbacks, #toggleSetupComments, #toggleSetupCompression
                {
                        border-color: #<?php echo(get_user_color('inner_border_color')); ?>;
                }

