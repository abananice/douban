<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>个人主页</title>
<link type="text/css" rel="stylesheet" href="css/dbNav.css">
<link type="text/css" rel="stylesheet" href="css/douyou.css">
<script src="https://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"></script>
<script>
	$(document).ready(function() {
		//账户菜单
		var moreItems = document.getElementsByClassName("more-items");
		moreItems.flag = 0;
		$(".more-items").hide();
		$(".bn-more").click(function() {
			if (!moreItems.flag) {
				$(".more-items").show();
				moreItems.flag = 1;
			} else {
				$(".more-items").hide();
				moreItems.flag = 0;
			}
		});
	})

	//搜索
	function validateForm() {
		if ($("#intp-query").val().length == 0) {
			alert("不能为空");
			return false;
		} else {
			return true;
		}
	}
</script>
</head>
<body>

	<div id="db-global-nav" class="global-nav">
		<div class="bd">

			<div class="top-nav-info">
				<ul>
					<li><a id="top-nav-doumail-link" href="#">豆邮</a></li>
					<li class="nav-user-account"><a class="bn-more" rel="off">
							<span>${sessionScope.account.name}的帐号</span><span class="arrow"></span>
					</a>
						<div class="more-items">
							<table cellpadding="0" cellspacing="0">
								<tbody>
									<tr>
										<td><a target="_blank" href="userPage?p=1">个人主页</a></td>
									</tr>
									<tr>
										<td><a>我的订单</a></td>
									</tr>
									<tr>
										<td><a>我的钱包</a></td>
									</tr>
									<tr>
										<td><a target="_blank" href="#">帐号管理</a></td>
									</tr>
									<tr>
										<td><a href="logout">退出登录</a></td>
									</tr>
								</tbody>
							</table>
						</div></li>
				</ul>
			</div>

			<div class="top-nav-reminder">
				<a>提醒</a>
			</div>

			<div class="top-nav-doubanapp">
				<a class="lnk-doubanapp">下载豆瓣客户端</a>
			</div>

			<div class="global-nav-items">
				<ul>
					<li class="on"><a>豆瓣</a></li>
					<li class=""><a>读书</a></li>
					<li class=""><a>电影</a></li>
					<li class=""><a>音乐</a></li>
					<li class=""><a>同城</a></li>
					<li class=""><a>小组</a></li>
					<li class=""><a>阅读</a></li>
					<li class=""><a>FM</a></li>
					<li class=""><a>时间</a></li>
					<li class=""><a>豆品</a></li>
					<li class=""><a>更多</a></li>
				</ul>
			</div>

		</div>
	</div>

	<div id="db-nav-sns" class="nav">
		<div class="nav-wrap">
			<div class="nav-primary">

				<div class="nav-logo">
					<a href="#">豆瓣社区</a>
				</div>

				<div class="nav-search">
					<form action="homePage" method="get"
						onsubmit="return validateForm();">
						<fieldset>
							<legend>搜索：</legend>
							<label for="inp-query" style="display: none;">搜索你感兴趣的内容和人...</label>
							<div class="inp">
								<input name="method" value="getSearchArticleByPage"
									type="hidden"> <input name="p" value="1" type="hidden">
								<input id="intp-query" name="q" size="22" maxlength="60"
									autocomplete="off" placeholder="搜索你感兴趣的内容和人...">
							</div>
							<div class="inp-btn">
								<input type="submit" value="搜索">
							</div>
						</fieldset>
					</form>
				</div>

				<div class="nav-items">
					<ul>
						<li><a href="#">首页</a></li>
						<li><a href="#">我的豆瓣</a></li>
						<li><a href="#"> 浏览发现 </a></li>
						<li><a href="#"> 话题广场 </a></li>
					</ul>
				</div>

			</div>
		</div>
	</div>

	<div id="wrapper">
		<div id="content">
			<h1>豆邮</h1>
			<div class="grid-16-8 clearfix">

				<div class="article">

					<div id="statuses">
						<form action="douYou" method="post">
							<h5>${requestScope.result}</h5>
							<h3>
								to:
								<textarea name="guest_email" placeholder="对方的邮箱" style="height: 15px">${param.guest_email}</textarea>
							</h3>
							<textarea class="content-input" placeholder="输入内容" name="msg"></textarea>
							<br> <input type="submit" value="发送" />
						</form>
					</div>

					<%--豆邮--%>
					<c:forEach var="douyou" items="${requestScope.douYouList}">
						<br>
						<div class="douyou-read">
							<h3>
								<img src="${douyou.avatar}" style="width: 20px; height: 20px">
								${douyou.name}
							</h3>
							<p>${douyou.msg}</p>
							<h6>${douyou.time}</h6>
						</div>
					</c:forEach>

					<%--页码--%>
					<div class="paginator">
						<%--根据当前页数来初始化页码--%>
						<c:choose>
							<c:when test="${param.p == 1}">
								<span class="prev"> &lt;前页 </span>
							</c:when>
							<c:otherwise>
								<a href="${requestScope.target}p=${param.p - 1}">&lt;前页</a>
							</c:otherwise>
						</c:choose>
						<%--初始化起始页码--%>
						<c:choose>
							<c:when test="${param.p - 5 > 0}">
								<c:set var="begin" value="${1 + param.p - 5}" />
							</c:when>
							<c:otherwise>
								<c:set var="begin" value="1" />
							</c:otherwise>
						</c:choose>
						<%--初始化终止页码--%>
						<c:choose>
							<c:when test="${begin + 8 < requestScope.totalPages}">
								<c:set var="end" value="${begin + 8}" />
							</c:when>
							<c:otherwise>
								<c:set var="end" value="${requestScope.totalPages}" />
							</c:otherwise>
						</c:choose>

						<c:forEach var="i" begin="${begin}" end="${end}" step="1">
							<c:choose>
								<c:when test="${param.p == i}">
									<span class="thispage">${i}</span>
								</c:when>
								<c:otherwise>
									<a href="${requestScope.target}p=${i}">${i}</a>
								</c:otherwise>
							</c:choose>
						</c:forEach>
						<span class="break">...</span>
						<c:choose>
							<c:when test="${param.p >= requestScope.totalPages}">
								<span class="next">后页 &gt; </span>
							</c:when>
							<c:otherwise>
								<a href="${requestScope.target}p=${param.p + 1}">后页&gt;</a>
							</c:otherwise>
						</c:choose>
					</div>

				</div>

			</div>


		</div>
	</div>
</body>
</html>