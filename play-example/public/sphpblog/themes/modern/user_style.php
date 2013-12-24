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

    $oppo = 'left';
    if ($theme_vars[ 'menu_align' ] == 'left') {
        $oppo = 'right';
    }

header("Content-Type: text/css");
?>
  /* OVERRIDE COLORS */

  /* GENERAL SETTINGS */
  body {
    background-color: #<?php echo(get_user_color('bg_color')); ?>;
    color: #<?php echo(get_user_color('txt_color')); ?>;
    font-family: Arial, Helvetica, Sans-Serif;
    font-size: 0.7em;
    margin: 0;
    padding: 0;
  }

  hr  
  {
    color: #<?php echo(get_user_color('inner_border_color')); ?>;
    background-color: #<?php echo(get_user_color('inner_border_color')); ?>;
  }

  p
  {
    margin: .8em 0 .8em 0;
  }


  input, select, option, textarea
  {
    font-size: 1em;
  }

  img {
    border-style: none;
  }

  /* HEADERS */
  h1, h2, h3, h4, h5, h6
  {
    font-family: Arial, Helvetica, Sans-Serif;
    font-weight: bold;
    margin: .3em 0 .3em 0;
  }
  
  /* TYPEOGRAPHY */
  
  a { text-decoration: none; }
  a:link, a:visited { color: #<?php echo(get_user_color('link_reg_color')); ?>; }
  a:hover { color: #<?php echo(get_user_color('link_hi_color')); ?>; }
  a:active { color: #<?php echo(get_user_color('link_down_color')); ?>; }

  code, pre {
    font-family: 'Courier New', Courier, Fixed;
    font-size: 1.2em;
  }

  pre {
    max-width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] ); ?>px;
    border-color: #<?php echo(get_user_color('inner_border_color')); ?>;
    overflow: auto;
    border: 1px dotted #777;
    padding: 10px;
  }
  
  /* HEADERS */
  
  h1, h2, h3, h4, h5, h6 {
    /* color: #<?php echo(get_user_color('headline_txt_color')); ?>; */
  }
  
  blockquote {
    color: #<?php echo(get_user_color('txt_color')); ?>;
    border-color: #<?php echo(get_user_color('inner_border_color')); ?>;
    padding-left: 1.5em;
    border-left: 5px solid #999999;
    margin: 1.5em 3.0em 0 1.0em;
  }
  
  /* LAYOUT / PLACEMENT */
  
  #page {
    background-color: #<?php echo(get_user_color('main_bg_color')); ?>;
    max-width: <?php echo $page_width; ?>px;
    margin: 10px auto 10px auto;
    border: 1px solid #<?php echo(get_user_color('border_color')); ?>;
  }
  
  #popup {
    max-width: 550px;
    margin: 10px auto 10px auto;
    border: 1px solid #<?php echo(get_user_color('border_color')); ?>;
    background-color: #<?php echo(get_user_color('main_bg_color')); ?>;
  }
  
  #header {
    background-image: url('<?php echo( $header_graphic ); ?>');
    background-repeat: no-repeat;
    border-style: none;
    min-height: 25px;
    padding: 105px 5px 5px 5px;
    border-bottom: 1px solid #<?php echo(get_user_color('border_color')); ?>;
    font-size: 1.3em;
    background-color: #<?php echo(get_user_color('header_bg_color')); ?>;
    color: #<?php echo(get_user_color('header_txt_color')); ?>;
  }

  #pagebody
  {
    margin: 0;
    padding: 0;
  }
  
  #footer {
    color: #<?php echo(get_user_color('footer_txt_color')); ?>;
    background: #<?php echo(get_user_color('footer_bg_color')); ?>;
    font-size: 0.8em;
/*    background-color: #996; */
    background-color: #<?php echo(get_user_color('footer_bg_color')); ?>;
    padding: 10px;
    border-top: 1px solid #<?php echo(get_user_color('border_color')); ?>;
    clear: both;

  }
  
  /* CONTENT */
  #maincontent
  {
    max-width: <?php echo( $theme_vars[ 'content_width' ]-25); ?>px;
    padding: 10px;
  }

#maincontent .entry,
#maincontent .static,
#maincontent .comment
{
  padding-bottom: 10px;
}


#maincontent .entry .blog_title,
#maincontent .static .blog_title,
#maincontent .comment .blog_title
{
  font-size: 1.3em;
  padding: .3em .5em;
  border-width: 1px 1px 0 1px;
  border-style: solid;
}

#maincontent .blog_title img
{
  /* padding: top right bottom left */
  padding: 0px 10px 0px 0px;
}

#maincontent .blog_title_buttons
{
  /* Edit and Delete buttons in the Blog Title */
  float: right;
  position: relative;
  top: -15px;
  right: 2px;
  display: inline;
}

#maincontent .entry .blog_body,
#maincontent .static .blog_body,
#maincontent .comment .blog_body
{
  line-height: 1.3em;
  padding: 10px;
  margin: 0;
  border: 1px solid #660;
}

#maincontent .entry .blog_body_clear,
#maincontent .static .blog_body_clear,
#maincontent .comment .blog_body_clear
{
  padding: 0px;
  border-color: #FFF;
  border-width: 0px;
  border-style: solid;
}

#maincontent .entry .blog_body_solid,
#maincontent .static .blog_body_solid,
#maincontent .comment .blog_body_solid
{
  padding: 1px;
  border-color: #660;
  background: #fff;
  border-width: 5px;
  border-style: solid;
}

#maincontent .entry .blog_date,
#maincontent .static .blog_date,
#maincontent .comment .blog_date
{
  font-size: .9em;
  margin-bottom: 1em;
}

#maincontent .entry .blog_comment,
#maincontent .static .blog_comment,
#maincontent .comment .blog_comment
{
  padding: .5em;
  border-width: 0 1px 1px 1px;
  border-style: solid;
}

  
  /* REGULAR ENTRY */
  #maincontent .entry .blog_title
  {
    border-color: #<?php echo(get_user_color('entry_border')); ?>;
    color: #<?php echo(get_user_color('entry_title_text')); ?>;
    background-color: #<?php echo(get_user_color('entry_title_bg')); ?>;
  }
  
  #maincontent .entry .blog_body
  {
    border-color: #<?php echo(get_user_color('entry_border')); ?>;
    color: #<?php echo(get_user_color('entry_text')); ?>;
    background-color: #<?php echo(get_user_color('entry_bg')); ?>;
  }

  #maincontent .entry .blog_body_solid  {
    border-color: #<?php echo(get_user_color('entry_border')); ?>;
    color: #<?php echo(get_user_color('entry_text')); ?>;
    background-color: #<?php echo(get_user_color('entry_border')); ?>;
  }
  
  #maincontent .entry .blog_comment
  {
    border-color: #<?php echo(get_user_color('entry_border')); ?>;
    color: #<?php echo(get_user_color('entry_text')); ?>;
    background-color: #<?php echo(get_user_color('entry_bg')); ?>;
  }
  
  #maincontent .entry .blog_date,
  #maincontent .static .blog_date,
  #maincontent .comment .blog_date
  {
    color: #<?php echo(get_user_color('date_txt_color')); ?>;
  }
  
  /* STATIC ENTRY */
  #maincontent .static .blog_title
  {
    border-color: #<?php echo(get_user_color('static_border')); ?>;
    color: #<?php echo(get_user_color('static_title_text')); ?>;
    background-color: #<?php echo(get_user_color('static_title_bg')); ?>;
  }
  
  #maincontent .static .blog_body
  {
    border-color: #<?php echo(get_user_color('static_border')); ?>;
    color: #<?php echo(get_user_color('static_text')); ?>;
    background-color: #<?php echo(get_user_color('static_bg')); ?>;
  }
  
  #maincontent .static .blog_comment
  {
    border-color: #<?php echo(get_user_color('static_border')); ?>;
    color: #<?php echo(get_user_color('static_text')); ?>;
    background-color: #<?php echo(get_user_color('static_bg')); ?>;
  }
  
  /* COMMENT ENTRY */
  #maincontent .comment .blog_title
  {
    border-color: #<?php echo(get_user_color('comment_border')); ?>;
    color: #<?php echo(get_user_color('comment_title_text')); ?>;
    background-color: #<?php echo(get_user_color('comment_title_bg')); ?>;
  }
  
  #maincontent .comment .blog_body
  {
    border-color: #<?php echo(get_user_color('comment_border')); ?>;
    color: #<?php echo(get_user_color('comment_text')); ?>;
    background-color: #<?php echo(get_user_color('comment_bg')); ?>;
  }
  
  #maincontent .comment .blog_comment
  {
    border-color: #<?php echo(get_user_color('comment_border')); ?>;
    color: #<?php echo(get_user_color('comment_text')); ?>;
    background-color: #<?php echo(get_user_color('comment_bg')); ?>;
  }
  
  /* SIDEBAR */
  
  #sidebar {
    max-width: <?php echo( $theme_vars[ 'menu_width' ]-20); ?>px;
    float: <?php echo $theme_vars[ 'menu_align' ]; ?>;
    padding: 10px;
    background-color: #<?php echo(get_user_color('menu_bg_color')); ?>;
    border-left: 1px #<?php echo(get_user_color('inner_border_color')); ?> solid;
    border-bottom: 1px #<?php echo(get_user_color('inner_border_color')); ?> solid;
    border-color: #<?php echo(get_user_color('inner_border_color')); ?>;
  }
  
  #sidebar .menu_body a { text-decoration: none; }
  #sidebar .menu_body a:link { color: #<?php echo(get_user_color('menu_link_reg_color')); ?>; }
  #sidebar .menu_body a:visited { color: #<?php echo(get_user_color('menu_link_reg_color')); ?>; }
  #sidebar .menu_body a:hover { color: #<?php echo(get_user_color('menu_link_hi_color')); ?>; }
  #sidebar .menu_body a:active { color: #<?php echo(get_user_color('menu_link_down_color')); ?>; }
  
  #sidebar .menu_title {
    border-color: #<?php echo(get_user_color('menu_border')); ?>;
    color: #<?php echo(get_user_color('menu_title_text')); ?>;
    background-color: #<?php echo(get_user_color('menu_title_bg')); ?>;
    font-size: 1em;
    padding: .3em .5em;
    margin: 0;
    border-width: 1px;
    border-style: solid;
  }

  #sidebar .menu_title a
  {
    text-decoration: none;
    color: inherit;
  }

  
  #sidebar .menu_body {
    padding: 10px;
    margin: 0;
    border-width: 0 1px 1px 1px;
    border-style: none solid solid solid;

    border-color: #<?php echo(get_user_color('menu_border')); ?>;
    color: #<?php echo(get_user_color('menu_text')); ?>;
    background-color: #<?php echo(get_user_color('menu_bg')); ?>;
  }

.blog_ip_address
{
  font-size: 0.8em;
}

#sidebar .calendar
{
  font-size: 1em;
}

#sidebar .calendar a
{
  font-weight: bold;
  text-decoration: none;
}

#archive_tree_menu li
{
  margin: 0;
  padding: .5em 0 0 0;
}

                div #toggleSetupLanguage, #toggleSetupGeneral, #toggleSetupEntries, #toggleSetupSidebar, #toggleSetupTrackbacks, #toggleSetupComments, #toggleSetupCompression
                {
                        border-color: #<?php echo(get_user_color('inner_border_color')); ?>;
                }

