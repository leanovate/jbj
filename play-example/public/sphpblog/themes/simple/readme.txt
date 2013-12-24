Name: Simple Template Theme
Author: Alexander Palmo
Version: 0.4.8

	
Description:
---------
This theme uses separate "template" files to create the layout. This should make it 
much easier to edit the theme. You no longer need to know PHP or edit the themes.php 
file to modify a theme (unless you want to, of course...)


Folder List:
---------
	simple/
		id.txt
		style.css
		themes.php
		user_style.php
		templates/
			comment.html
			entry.html
			main_layout.html
			menu_block.html
			(menu_block_no_expand.html)
			popup_layout.html
			static.html
		colors/
			(Scheme color files...)
		images/
			(Image files...)


Usage:
-----
To edit/modify a theme, you will need to edit the 6 files in the "templates/" folder.

A template is simply an HTML file with special tags in it. The tags look like %this% and
get replaced by real content when you view the page. There is quite a long list of tags.


Here is a complete list:

Content Tags
-----------
The following tags are the "main" tags.
			main_layout.html
			popup_layout.html

%blog_title%
%content%
%footer%


General page layout tags
-------------------
The following tags are mostly "utility" tags.
			main_layout.html
			popup_layout.html

%page_width%
%menu_width%
%content_width%
%popup_width%
%image_path%


Blog entries / Comments / Static entries
-------------------------------
The following tags are used for all of the "entry" content:
			comment.html
			entry.html
			static.html
			
%rdf%
%subject%
%id%
%date%
%categories%
%edit_button%
%delete_button%
%add_comment%
%views%
%trackbacks%
%permalink%
%relatedlink%
%ipaddress%
%ratings%
%content%


Sidebar Menu / Widgets
------------------
These tags are used for the sidebar:

Contains all the "widgets"
%menu%

-- or -->

Build your own menu:
%widget_avatar% -- or --> %widget_avatar=alternate_template.html%
%widget_links%
%widget_user%
%widget_setup%
%widget_custom%
%widget_calendar%
%widget_archive_tree%
%widget_categories%
%widget_search%
%widget_counter%
%widget_recent_entries%
%widget_recent_comments%
%widget_recent_trackbacks%
%widget_badges%


Widgets
------
Inside each menu block, you can use these tags:
			menu_block.html
			(menu_block_no_expand.html)
			
%title%
%content%
%comment%
%id%
%twisty%


Colors
-----
The following tags get replaced with HEX color values that look like this: FFFFFF

%main_bg_color%
%header_bg_color%
%footer_bg_color%
%menu_bg%
%menu_title_bg%
%menu_border%
%menu_title_text%
%menu_text%
%menu_link_reg_color%
%menu_link_hi_color%
%menu_link_down_color%
%inner_border_color%
%txt_color%
%headline_txt_color%
%date_txt_color%
%header_txt_color%
%footer_txt_color%
%link_reg_color%
%link_hi_color%
%link_down_color%
%entry_title_text%