package com.xxm.douban.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import com.xxm.douban.bean.Msg;
import com.xxm.douban.entity.Account;
import com.xxm.douban.entity.Article;
import com.xxm.douban.util.DbUtil;

public class ArticleDAOJdbcImpl implements ArticleDAO {
	private DataSource dataSource;

	private Connection con = null;

	private PreparedStatement stmt = null;

	public ArticleDAOJdbcImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	// 添加文章
	@Override
	public Msg addArticle(Article article) {
		try {
			con = dataSource.getConnection();
			String sql = "insert into t_article(author_email, title, type, content, picture_urls, modify_time) values(?, ?, ?, ?, ?, ?)";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, article.getAuthor_email());
			stmt.setString(2, article.getTitle());
			stmt.setString(3, article.getType());
			stmt.setString(4, article.getContent());
			stmt.setString(5, article.getPicture_urls());
			stmt.setString(6, article.getModify_time());

			// 判断执行插入语句后受影响语句是否大于0
			if (stmt.executeUpdate() > 0) {
				return new Msg("发布文章成功", null);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtil.close(stmt, con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new Msg("发布文章失败", null);
	}
	
	//在限制条件下获取文章总数
	@Override
	public Msg getArticleCount(String limit) {
		try {
			con = dataSource.getConnection();
			String sql = "select count(*) from " + limit;
			stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Msg("获取文章总数成功", rs.getInt(1));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtil.close(stmt, con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new Msg("获取文章总数失败", null);
	}

	// 通过当前页数获取文章
	@Override
	public Msg getArticleByPage(String currentPage) {
		try {
			Article article = null;
			List<Article> list = new ArrayList<>();
			con = dataSource.getConnection();
			//sql语句功能：把用户转发的文章也弄进来和其他文章按hot和时间来排序显示
			String sql = "SELECT * FROM"
					+ "( SELECT t_article.id, t_article.author_email, t_article.title, t_article.type, "
					+ "t_article.content, t_article.picture_urls, t_article.modify_time, t_article.hot, "
					+ "t_account.name, t_account.avatar, t_article.author_email AS origin_author "
					+ "FROM t_article, t_account "
					+ "WHERE t_article.author_email = t_account.email "
					+ "UNION "
					+ "SELECT t_article.id, t_article_info.user_email AS author_email, t_article.title, "
					+ "t_article.type, t_article.content, t_article.picture_urls, t_article.modify_time, t_article.hot, "
					+ "t_account.`name`, t_account.avatar, t_article.author_email AS origin_author "
					+ "FROM t_article, t_account, t_article_info "
					+ "WHERE t_article_info.forword = 1 "
					+ "AND t_article_info.article_id = t_article.id "
					+ "AND t_article_info.user_email = t_account.email) "
					+ "AS t_sub "
					+ "ORDER BY hot DESC, modify_time DESC LIMIT ?, 4";

			stmt = con.prepareStatement(sql);
			stmt.setInt(1, (Integer.valueOf(currentPage) - 1) * 4);// 每页四条记录
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				article = new Article();
				article.setId(rs.getString("id"));
				article.setAuthor_email(rs.getString("author_email"));
				article.setTitle(rs.getString("title"));
				article.setType(rs.getString("type"));
				article.setContent(rs.getString("content"));
				article.setPicture_urls(rs.getString("picture_urls"));
				article.setModify_time(rs.getString("modify_time"));
				article.setHot(rs.getString("hot"));
				article.setAvatar(rs.getNString("avatar"));
				article.setName(rs.getNString("name"));
				article.setOrigin_author(rs.getNString("origin_author"));
				list.add(article);
			}
			if (list.isEmpty()) {
				return new Msg("无文章", null);
			} else {
				return new Msg("分页获取文章成功", list);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtil.close(stmt, con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new Msg("分页获取文章失败", null);
	}	

	// 通过当前页数获取某个用户的文章
	@Override
	public Msg getUserArticleByPage(String currentPage, String limit) {
		try {
			Article article = null;
			List<Article> list = new ArrayList<>();
			con = dataSource.getConnection();
			String sql = "select t_article.*, t_account.name, t_account.avatar from t_article, t_account "
					+ limit
					+ " order by hot desc, modify_time desc limit ?, 4 ";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, (Integer.valueOf(currentPage) - 1) * 4);// 每页四条记录
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				article = new Article();
				article.setId(rs.getString("id"));
				article.setAuthor_email(rs.getString("author_email"));
				article.setTitle(rs.getString("title"));
				article.setType(rs.getString("type"));
				article.setContent(rs.getString("content"));
				article.setPicture_urls(rs.getString("picture_urls"));
				article.setModify_time(rs.getString("modify_time"));
				article.setHot(rs.getString("hot"));
				article.setAvatar(rs.getString("avatar"));
				article.setName(rs.getString("name"));
				list.add(article);
			}
			if (list.isEmpty()) {
				return new Msg("无文章", null);
			} else {
				return new Msg("分页获取某个用户的文章成功", list);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtil.close(stmt, con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new Msg("分页获取某个用户的文章失败", null);
	}

	// 搜索文章
	@Override
	public Msg getSearchArticleByPage(String currentPage, String keyWord) {
		try {
			Article article = null;
			List<Article> list = new ArrayList<>();
			con = dataSource.getConnection();
			String sql = " select t_article.*, t_account.name, t_account.avatar  from t_article "
					+ "inner join t_account on t_article.author_email = t_account.email "
					+ "where t_article.title like concat('%',?,'%')" + "order by hot desc, modify_time desc limit ?, 4";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, keyWord);
			stmt.setInt(2, (Integer.valueOf(currentPage) - 1) * 4);// 每页四条记录
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				article = new Article();
				article.setId(rs.getString("id"));
				article.setAuthor_email(rs.getString("author_email"));
				article.setTitle(rs.getString("title"));
				article.setType(rs.getString("type"));
				article.setContent(rs.getString("content"));
				article.setPicture_urls(rs.getString("picture_urls"));
				article.setModify_time(rs.getString("modify_time"));
				article.setHot(rs.getString("hot"));
				article.setAvatar(rs.getNString("avatar"));
				article.setName(rs.getNString("name"));
				list.add(article);
			}
			if (list.isEmpty()) {
				return new Msg("无文章", null);
			} else {
				return new Msg("分页搜索文章成功", list);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtil.close(stmt, con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new Msg("分页搜索文章失败", null);
	}

	// 获取搜索文章总数
	@Override
	public Msg getSearchArticleCount(String keyWord) {
		try {
			con = dataSource.getConnection();
			String sql = "select count(*) from t_article where title like concat('%',?,'%')";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, keyWord);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Msg("获取搜索文章总数成功", rs.getInt(1));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtil.close(stmt, con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new Msg("获取搜索文章总数失败", null);
	}

	// 分类获取文章总数
	@Override
	public Msg getTypeArticleCount(String type) {
		try {
			con = dataSource.getConnection();
			String sql = "select count(*) from t_article where type like concat('%',?,'%')";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, type);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Msg("分类获取文章总数成功", rs.getInt(1));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtil.close(stmt, con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new Msg("分类获取文章总数失败", null);
	}

	// 分类获取文章
	@Override
	public Msg getTypeArticleByPage(String currentPage, String type) {
		try {
			Article article = null;
			List<Article> list = new ArrayList<>();
			con = dataSource.getConnection();
			String sql = " select t_article.*, t_account.name, t_account.avatar  from t_article "
					+ "inner join t_account on t_article.author_email = t_account.email "
					+ "where t_article.type like concat('%',?,'%')" + "order by hot desc, modify_time desc limit ?, 4";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, type);
			stmt.setInt(2, (Integer.valueOf(currentPage) - 1) * 4);// 每页四条记录
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				article = new Article();
				article.setId(rs.getString("id"));
				article.setAuthor_email(rs.getString("author_email"));
				article.setTitle(rs.getString("title"));
				article.setType(rs.getString("type"));
				article.setContent(rs.getString("content"));
				article.setPicture_urls(rs.getString("picture_urls"));
				article.setModify_time(rs.getString("modify_time"));
				article.setHot(rs.getString("hot"));
				article.setAvatar(rs.getNString("avatar"));
				article.setName(rs.getNString("name"));
				list.add(article);
			}
			if (list.isEmpty()) {
				return new Msg("无文章", null);
			} else {
				return new Msg("分类获取文章成功", list);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtil.close(stmt, con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new Msg("分类获取文章失败", null);
	}

	// 置顶/取消置顶
	@Override
	public Msg setArticleHot(String id, int hot) {
		try {
			con = dataSource.getConnection();
			String sql = "update t_article set hot = ? where id = ?";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, hot);
			stmt.setString(2, id);

			// 判断执行插入语句后受影响语句是否大于0
			if (stmt.executeUpdate() > 0) {
				return new Msg("设置成功", null);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtil.close(stmt, con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new Msg("设置失败", null);
	}

	// 删除文章
	@Override
	public Msg deleteArticle(String id, String email) {
		try {
			con = dataSource.getConnection();
			String sql = "delete from t_article where id = ? and author_email = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, id);
			stmt.setString(2, email);

			// 判断执行删除语句后受影响语句是否大于0
			if (stmt.executeUpdate() > 0) {
				return new Msg("删除文章成功", null);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtil.close(stmt, con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new Msg("删除文章失败", null);
	}

	// 获取删除文章的图片路径
	@Override
	public Msg getArticlePics(String id, String email) {
		try {
			con = dataSource.getConnection();
			String sql = "select picture_urls from t_article where id = ? and author_email = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, id);
			stmt.setString(2, email);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Msg("获取删除文章的图片路径成功", rs.getString("picture_urls"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtil.close(stmt, con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new Msg("获取删除文章的图片路径失败", null);
	}

	//获取收藏转发点赞的文章
	@Override
	public Msg getCollectArticleByPage(String currentPage, Account account, String method) {
		try {
			Article article = null;
			List<Article> list = new ArrayList<>();
			con = dataSource.getConnection();
			String sql = "SELECT t_article.*, t_account.name, t_account.avatar FROM t_article, t_article_info, t_account "
					+ "WHERE t_article_info.user_email = ?  "
					+ "AND t_article_info." + method +" = 1 "
					+ "AND t_article_info.article_id = t_article.id "
					+ "AND t_article.author_email = t_account.email "
					+ "ORDER BY hot DESC, modify_time DESC LIMIT ?, 4 ";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, account.getEmail());
			stmt.setInt(2, (Integer.valueOf(currentPage) - 1) * 4);// 每页四条记录
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				article = new Article();
				article.setId(rs.getString("id"));
				article.setAuthor_email(rs.getString("author_email"));
				article.setTitle(rs.getString("title"));
				article.setType(rs.getString("type"));
				article.setContent(rs.getString("content"));
				article.setPicture_urls(rs.getString("picture_urls"));
				article.setModify_time(rs.getString("modify_time"));
				article.setHot(rs.getString("hot"));
				article.setAvatar(rs.getString("avatar"));
				article.setName(rs.getString("name"));
				list.add(article);
			}
			if (list.isEmpty()) {
				return new Msg("无文章", null);
			} else {
				return new Msg("获取收藏转发的文章成功", list);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtil.close(stmt, con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new Msg("获取收藏转发的文章失败", null);
	}

	//修改文章信息
	@Override
	public Msg modifyArticle(Article article) {
		try {
			con = dataSource.getConnection();
			String sql = "update t_article set title = ?, type = ?, content = ?, picture_urls = ?, modify_time = ? where id = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, article.getTitle());
			stmt.setString(2, article.getType());
			stmt.setString(3, article.getContent());
			stmt.setString(4, article.getPicture_urls());
			stmt.setString(5, article.getModify_time());
			stmt.setString(6, article.getId());

			// 判断执行删除语句后受影响语句是否大于0
			if (stmt.executeUpdate() > 0) {
				return new Msg("修改文章信息成功", null);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtil.close(stmt, con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new Msg("修改文章信息失败", null);
	}

}
