package com.alex.blog_back.service;

import com.alex.blog_back.auth.AppUser;
import com.alex.blog_back.model.Article;
import com.alex.blog_back.model.Categorie;
import com.alex.blog_back.repo.AppUserRepo;
import com.alex.blog_back.repo.ArticleRepo;
import com.alex.blog_back.repo.CategorieRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.lang.module.FindException;
import java.security.Principal;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepo articleRepo;
    private final AppUserRepo appUserRepo;
    private final CategorieRepo categorieRepo;

    @Override
    public Optional<Article> findArticleById(Long id) {
        return articleRepo.findById(id);
    }

    @Override
    public List<Article> findAllArticle() {
        return articleRepo.findAll();
    }

    @Override
    public List<Categorie> findAllCategorie() {
        return categorieRepo.findAll();
    }


    @Override
    public Article addArticle(Article article) {
        return articleRepo.save(article);
    }

    @Override
    public void updateArticle(Long id, Article article) {

    }

    @Override
    public void deleteArticleByAuthorAndIdService(String username, Long id) throws IllegalAccessException {
        Article article = articleRepo.findById(id)
                .orElseThrow(() -> new NullPointerException("Cet article n'éxiste pas"));
        if (username.equals(article.getAuteur().getUsername()) &&
                username.equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        ) {
            articleRepo.deleteById(id);
        } else
            throw new IllegalAccessException("Action non autorisée pour cet utilisateur");

    }

    @Override
    public Article addArticleWithAuteurName(Article article, String username, String categorie) throws IllegalAccessException {
        //Vérification username nouvel article == username authentifié
        if (!Objects.equals(username, SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
            throw new IllegalAccessException("Mauvais utilisateur");
        }

        //Verif existance username puis ajout dans l'article
        article.setAuteur(
                appUserRepo.findByUsername(username)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Erreur, aucun auteur défini"))
        );

        //Verif existance categorie puis ajout dans l'article
        article.setCategorie(
                categorieRepo.findCategorieByNom(categorie)
                        .orElse(null));


        //Enreg. date de création article
        DateFormat mediumDateFormat = (DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.MEDIUM));
        String date = mediumDateFormat.format(new Date());
        article.setDate(
                ("Le " + date.substring(0, date.length() - 3))
                        .replace(":", "h")
        );

        return articleRepo.save(article);
    }


    @Override
    public Optional<List<Article>> findAllArticleByCategorieServ(String categorie) {
        Categorie currentCategorie = categorieRepo.findCategorieByNom(categorie)
                .orElseThrow(() -> new NullPointerException("Cette catégorie n'existe pas"));
        return articleRepo.findByCategorie(currentCategorie);
    }

    @Override
    public Article modifyArticle(String username, Article newArticle, String categorie) throws IllegalAccessException {
        if (!Objects.equals(username, SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
            throw new IllegalAccessException("Mauvais utilisateur");
        }

        Article currentArticle = articleRepo.findById(newArticle.getId())
                .orElseThrow(() -> new NullPointerException("Cet article n'existe pas"));

        if (newArticle.getTitre() != null) {
            currentArticle.setTitre(newArticle.getTitre());
        }
        if (newArticle.getContenu() != null) {
            currentArticle.setContenu(newArticle.getContenu());
        }
        if (categorie != null) {
            currentArticle.setCategorie(categorieRepo.findCategorieByNom(categorie)
                    .orElseThrow(() -> new NullPointerException("Cette catégorie n'existe pas")));
        }

        DateFormat mediumDateFormat = (DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.MEDIUM));
        String date = mediumDateFormat.format(new Date());
        currentArticle.setDate(
                ("Modifié le " + date.substring(0, date.length() - 3))
                        .replace(":", "h")
        );

        return articleRepo.save(currentArticle);
    }


/* @Override
    public Article addArticleWithAuteurId(Article article, Long idAuteur) {
        AppUser testUser = appUserRepo.findById(idAuteur).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Erreur, aucun d'auteur défini"));

        DateFormat mediumDateFormat = (DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.MEDIUM));
        String date = mediumDateFormat.format(new Date());
        article.setDate(
                ("Le " + date.substring(0, date.length() - 3))
                        .replace(":", "h")
        );

        article.setAuteur(testUser);

        return articleRepo.save(article);
    }*/

}
