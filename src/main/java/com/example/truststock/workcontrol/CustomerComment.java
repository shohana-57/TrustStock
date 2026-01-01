package com.example.truststock.workcontrol;

import com.example.truststock.db.Database;
import com.example.truststock.staff.CommentItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerComment {

    public Button goQP;
    @FXML private TableView<CommentItem> tableComments;
    @FXML private TableColumn<CommentItem, Integer> colId;
    @FXML private TableColumn<CommentItem, String> colProduct;
    @FXML private TableColumn<CommentItem, String> colComment;
    @FXML private TableColumn<CommentItem, String> colDate;

    private final ObservableList<CommentItem> commentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> c.getValue().idProperty().asObject());
        colProduct.setCellValueFactory(c -> c.getValue().productNameProperty());
        colComment.setCellValueFactory(c -> c.getValue().commentProperty());
        colDate.setCellValueFactory(c -> c.getValue().dateProperty());

        loadComments();
        tableComments.setItems(commentList);
    }

    private void loadComments() {
        commentList.clear();
        String sql = "SELECT c.id, p.name, c.comment_text, c.comment_date " +
                "FROM product_comments c " +
                "JOIN products p ON c.product_id = p.id " +
                "ORDER BY c.comment_date DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                commentList.add(new CommentItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("comment_text"),
                        rs.getString("comment_date")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteComment() {
        CommentItem selected = tableComments.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String sql = "DELETE FROM product_comments WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, selected.getId());
            ps.executeUpdate();
            commentList.remove(selected);
            new Alert(Alert.AlertType.INFORMATION, "Comment deleted").show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to delete comment").show();
        }
    }

    public void QualityPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/truststock/QualityCheck.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage)goQP.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
