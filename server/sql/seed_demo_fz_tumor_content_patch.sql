SET NAMES utf8mb4;
-- Patch DEMO-FZ-TUMOR content pages to match reference image galleries.
SET @aid = (SELECT activity_id FROM yc_activity WHERE activity_code = 'DEMO-FZ-TUMOR' LIMIT 1);

UPDATE yc_activity_grid
SET link_type='content', content_type='image',
    content_url='http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/1bde2cd71aa5476688764c4634c5f0ad.jpg',
    content='["http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/1bde2cd71aa5476688764c4634c5f0ad.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/1a3aa5fd66364d0780300108088d47d7.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/fc71cedf8d944fa7af924d9de84f930d.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/a4aeb34b74ba4eeb9314f7bdfb535392.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/d8ebde116dc240dc9d102eaae6178243.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/2f5c0efb174942c8be45eb3206640356.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/a8a25da1bf2541ff873267d29a77bef9.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/d4906b7b7e574d0b82c67fe1b802d27f.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/dcb427d4635d49019cd38a0c07380a30.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/bd3082c969c744f5ab3bce66cfd899de.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/bf88e648366d4572a17280542397e8fb.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/5b4944a906cd4934a781ac110493e622.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/3327b10a07bb45f4b7c3522e20e18331.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/0cf894659eb24a7aa5469e83eb916a47.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/98a6c3a70155416b8e54d9b70d88631d.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/155aab00ab454cb6bee60f81973c0c92.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/5adbebb55f0141fa8628c79313b9e5ea.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/83dc898fae89498f8b76a52660401eda.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/542bec2aba5f40159440ab841b499d78.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/fed6f3cb791744adac00a8e40b18937c.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/bb167856f49643a78bb9381a2c19f710.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/9db2eb55c4604138848fa86635ae7eb7.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/a88daea963884e8289f96fffb43431f5.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/061db6a40b0f452ea3dc03c95412302c.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/e4f447aedee44ac291161afb9361642d.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/3141af87065d4500b0932db36813ff3f.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/628de2f5128a43899283123585b50e49.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/1378889398374e6cabfbdea3f7077e3a.jpg","http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/74cd0ee826494698b22f4bf940cec632.jpg"]'
WHERE activity_id=@aid AND title='会议简介';

UPDATE yc_activity_grid
SET link_type='content', content_type='image',
    content_url='http://mpjoy.oss-cn-beijing.aliyuncs.com/20251206/62b76ccef73e4f26ae86401b3a81636b.png',
    content='["http://mpjoy.oss-cn-beijing.aliyuncs.com/20251206/62b76ccef73e4f26ae86401b3a81636b.png"]'
WHERE activity_id=@aid AND title='学分须知';

UPDATE yc_activity_grid
SET link_type='content', content_type='image',
    content_url='http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/fc5a3faea3594d818f3e9cae6d11476b.jpg',
    content='["http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/fc5a3faea3594d818f3e9cae6d11476b.jpg"]'
WHERE activity_id=@aid AND title='大会议程';

UPDATE yc_activity_grid
SET link_type='content', content_type='image',
    content_url='http://mpjoy.oss-cn-beijing.aliyuncs.com/20251206/1f7c955089d74bf8b872d7c785bcc7c5.png',
    content='["http://mpjoy.oss-cn-beijing.aliyuncs.com/20251206/1f7c955089d74bf8b872d7c785bcc7c5.png"]'
WHERE activity_id=@aid AND title='会后考试';
