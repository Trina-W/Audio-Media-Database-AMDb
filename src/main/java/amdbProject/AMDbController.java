package amdbProject;

import java.util.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import modelAttributes.*;

@Controller
@RequestMapping("/amdb")
public class AMDbController {

    @GetMapping
    public String get(Model model) {
        model.addAttribute("song", new Song());
        model.addAttribute("recordLabel", new RecordLabel());
        model.addAttribute("musicArtist", new MusicArtist());
        model.addAttribute("musicRelease", new MusicRelease());
        model.addAttribute("podcastHost", new PodcastHost());
        model.addAttribute("podcast", new Podcast());
        model.addAttribute("podcastEpisode", new PodcastEpisode());
        model.addAttribute("publisher", new Publisher());
        model.addAttribute("audioBook", new AudioBook());
        model.addAttribute("author", new Author());
        model.addAttribute("narrator", new Narrator());

        // Important: Initialize this attribute on GET requests
        model.addAttribute("searchPerformed", false);  // Indicate that search has not yet been performed

        return "home";
    }

    @PostMapping
    public String post(@RequestParam String selectedCategory, @ModelAttribute("song") Song song, @ModelAttribute("recordLabel") RecordLabel recordLabel,
                       @ModelAttribute("musicArtist") MusicArtist musicArtist, @ModelAttribute("musicRelease") MusicRelease musicRelease,
                       @ModelAttribute("podcastHost") PodcastHost podcastHost, @ModelAttribute("podcast") Podcast podcast,
                       @ModelAttribute("podcastEpisode") PodcastEpisode podcastEpisode, @ModelAttribute("publisher") Publisher publisher,
                       @ModelAttribute("audioBook") AudioBook audioBook, @ModelAttribute("author") Author author,
                       @ModelAttribute("narrator") Narrator narrator, Model model) {
        model.addAttribute("selectedCategory", selectedCategory);
        model.addAttribute("searchPerformed", true);

        System.out.println("\n*******************************************************************************************************************");

        TranslatorService translator = new TranslatorService();

        //testing output of allResults
        ArrayList<LinkedHashMap<String, String>> allSongResults = translator.retrieveSong(song);
        for (LinkedHashMap<String, String> hm : allSongResults) {
            System.out.println(hm);
        }
        model.addAttribute("allSongResults", allSongResults);

        ArrayList<LinkedHashMap<String, String>> allRLResults = translator.retrieveRecordLabel(recordLabel);
        for (LinkedHashMap<String, String> hm : allRLResults) {
            System.out.println(hm);
        }
        model.addAttribute("allRLResults", allRLResults);

        ArrayList<LinkedHashMap<String, String>> allMAResults = translator.retrieveMusicArtist(musicArtist);
        for (LinkedHashMap<String, String> hm : allMAResults) {
            System.out.println(hm);
        }
        model.addAttribute("allMAResults", allMAResults);

        ArrayList<LinkedHashMap<String, String>> allMRResults = translator.retrieveMusicRelease(musicRelease);
        for (LinkedHashMap<String, String> hm : allMRResults) {
            System.out.println(hm);
        }
        model.addAttribute("allMRResults", allMRResults);

        ArrayList<LinkedHashMap<String, String>> allPHResults = translator.retrievePodcastHost(podcastHost);
        for (LinkedHashMap<String, String> hm : allPHResults) {
            System.out.println(hm);
        }
        model.addAttribute("allPHResults", allPHResults);

        ArrayList<LinkedHashMap<String, String>> allPCResults = translator.retrievePodcast(podcast);
        for (LinkedHashMap<String, String> hm : allPCResults) {
            System.out.println(hm);
        }
        model.addAttribute("allPCResults", allPCResults);

        ArrayList<LinkedHashMap<String, String>> allPEResults = translator.retrievePodcastEpisode(podcastEpisode);
        for (LinkedHashMap<String, String> hm : allPEResults) {
            System.out.println(hm);
        }
        model.addAttribute("allPEResults", allPEResults);

        ArrayList<LinkedHashMap<String, String>> allPResults = translator.retrievePublisher(publisher);
        for (LinkedHashMap<String, String> hm : allPResults) {
            System.out.println(hm);
        }
        model.addAttribute("allPResults", allPResults);

        ArrayList<LinkedHashMap<String, String>> allABResults = translator.retrieveAudioBook(audioBook);
        for (LinkedHashMap<String, String> hm : allABResults) {
            System.out.println(hm);
        }
        model.addAttribute("allABResults", allABResults);

        ArrayList<LinkedHashMap<String, String>> allAResults = translator.retrieveAuthor(author);
        for (LinkedHashMap<String, String> hm : allAResults) {
            System.out.println(hm);
        }
        model.addAttribute("allAResults", allAResults);

        ArrayList<LinkedHashMap<String, String>> allNResults = translator.retrieveNarrator(narrator);
        for (LinkedHashMap<String, String> hm : allNResults) {
            System.out.println(hm);
        }
        model.addAttribute("allNResults", allNResults);

        boolean noResults = allSongResults.isEmpty() && allRLResults.isEmpty() && allMAResults.isEmpty()
                && allMRResults.isEmpty() && allPHResults.isEmpty() && allPCResults.isEmpty()
                && allPEResults.isEmpty() && allPResults.isEmpty() && allABResults.isEmpty()
                && allAResults.isEmpty() && allNResults.isEmpty();

        model.addAttribute("noResults", noResults);  // Add this flag to the model

        // Return the same view as GET request
        return "home"; // Assuming "home-3" is your main page with the form
    }
}
