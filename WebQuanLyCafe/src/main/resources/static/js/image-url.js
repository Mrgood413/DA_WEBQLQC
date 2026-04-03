/**
 * Thu nhỏ ảnh Unsplash (imgix) qua tham số w/fit/q để giảm dung lượng tải.
 * Dùng chung cho menu, giỏ hàng, staff/admin.
 */
(function (global) {
  function optimize(url, options) {
    if (url == null || typeof url !== "string") return url;
    var u = url.trim();
    if (u === "" || u.indexOf("data:") === 0) return u;

    var opts = options || {};
    var maxW = opts.maxW != null ? opts.maxW : 640;

    if (u.indexOf("images.unsplash.com") === -1) return u;

    try {
      var parsed = new URL(u);
      var wParam = parsed.searchParams.get("w");
      if (wParam) {
        var wn = parseInt(wParam, 10);
        if (!isNaN(wn) && wn > maxW) {
          parsed.searchParams.set("w", String(maxW));
        }
        if (!parsed.searchParams.has("fit")) parsed.searchParams.set("fit", "crop");
        if (!parsed.searchParams.has("auto")) parsed.searchParams.set("auto", "format");
      } else {
        parsed.searchParams.set("w", String(maxW));
        parsed.searchParams.set("fit", "crop");
        parsed.searchParams.set("auto", "format");
        if (!parsed.searchParams.has("q")) parsed.searchParams.set("q", "78");
      }
      return parsed.toString();
    } catch (e) {
      var sep = u.indexOf("?") >= 0 ? "&" : "?";
      return u + sep + "w=" + maxW + "&fit=crop&auto=format&q=78";
    }
  }

  global.WebCafeImageUrl = { optimize: optimize };
})(typeof window !== "undefined" ? window : this);
