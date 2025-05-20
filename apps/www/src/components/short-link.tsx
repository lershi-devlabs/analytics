"use client" 

import * as React from "react"
import { useState, useEffect, useRef } from "react";
import { Globe, Paperclip, Send } from "lucide-react";
import { AnimatePresence, motion } from "motion/react";
import { useRouter } from "next/navigation";

 
const PLACEHOLDERS = [
  "https://example.com",
  "https://mywebsite.com",
  "https://blog.example.org",
  "https://shop.example.net",
  "https://docs.example.io",
  "https://app.example.co",
];
 
const ShortLinkInput = () => {
  const [placeholderIndex, setPlaceholderIndex] = useState(0);
  const [showPlaceholder, setShowPlaceholder] = useState(true);
  const [isActive, setIsActive] = useState(false);
  const [thinkActive, setThinkActive] = useState(false);
  const [customAliasActive, setCustomAliasActive] = useState(false);
  const [inputValue, setInputValue] = useState("");
  const [shortUrl, setShortUrl] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [customDomain, setCustomDomain] = useState("");
  const [customAlias, setCustomAlias] = useState("");
  const [token, setToken] = useState<string | null>(null);
  const wrapperRef = useRef<HTMLDivElement>(null);

  const apiUrl = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";
  const router = useRouter();
 
  // Cycle placeholder text when input is inactive
  useEffect(() => {
    if (isActive || inputValue) return;
 
    const interval = setInterval(() => {
      setShowPlaceholder(false);
      setTimeout(() => {
        setPlaceholderIndex((prev) => (prev + 1) % PLACEHOLDERS.length);
        setShowPlaceholder(true);
      }, 400);
    }, 3000);
 
    return () => clearInterval(interval);
  }, [isActive, inputValue]);
 
  // Close input when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        wrapperRef.current &&
        !wrapperRef.current.contains(event.target as Node)
      ) {
        if (!inputValue) setIsActive(false);
      }
    };
 
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [inputValue]);
 
  useEffect(() => {
    const stored = localStorage.getItem("token");
    if (stored) setToken(stored);
  }, []);
 
  const handleActivate = () => setIsActive(true);
 
  const handleSubmit = async () => {
    setError("");
    setShortUrl("");
    if (!/^https?:\/\//.test(inputValue)) {
      setError("Please enter a valid URL starting with http:// or https://");
      return;
    }
    setLoading(true);
    try {
      const res = await fetch(`${apiUrl}/api/urls/shorten`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
        body: JSON.stringify({
          originalUrl: inputValue,
          customDomain: thinkActive ? customDomain : undefined,
          customAlias: customAliasActive ? customAlias : undefined,
          projectId: "d5d3274a-bd2e-4e07-9f3a-fddc0e75569a" // TODO: Replace with actual projectId logic
        }),
      });
      if (!res.ok) throw new Error("Failed to shorten URL");
      router.push("/myurls");
    } catch {
      setError("Failed to shorten URL");
    } finally {
      setLoading(false);
    }
  };
 
  const containerVariants = {
    collapsed: {
      height: 68,
      boxShadow: "0 2px 8px 0 rgba(0,0,0,0.08)",
      transition: { type: "spring", stiffness: 120, damping: 18 },
    },
    expanded: {
      height: 128,
      boxShadow: "0 8px 32px 0 rgba(0,0,0,0.16)",
      transition: { type: "spring", stiffness: 120, damping: 18 },
    },
  };
 
  const placeholderContainerVariants = {
    initial: {},
    animate: { transition: { staggerChildren: 0.025 } },
    exit: { transition: { staggerChildren: 0.015, staggerDirection: -1 } },
  };
 
  const letterVariants = {
    initial: {
      opacity: 0,
      filter: "blur(12px)",
      y: 10,
    },
    animate: {
      opacity: 1,
      filter: "blur(0px)",
      y: 0,
      transition: {
        opacity: { duration: 0.25 },
        filter: { duration: 0.4 },
        y: { type: "spring", stiffness: 80, damping: 20 },
      },
    },
    exit: {
      opacity: 0,
      filter: "blur(12px)",
      y: -10,
      transition: {
        opacity: { duration: 0.2 },
        filter: { duration: 0.3 },
        y: { type: "spring", stiffness: 80, damping: 20 },
      },
    },
  };
 
  return (
    <div className="w-full min-h-screen flex justify-center items-center text-black">
      <motion.div
        ref={wrapperRef}
        className="w-full max-w-3xl"
        variants={containerVariants}
        animate={isActive || inputValue ? "expanded" : "collapsed"}
        initial="collapsed"
        style={{ overflow: "hidden", borderRadius: 32, background: "#fff" }}
        onClick={handleActivate}
      >
        <div className="flex flex-col items-stretch w-full h-full">
          {/* Input Row */}
          <div className="flex items-center gap-2 p-3 rounded-full bg-white max-w-3xl w-full">
            <button
              className="p-3 rounded-full hover:bg-gray-100 transition"
              title="Attach file"
              type="button"
              tabIndex={-1}
            >
              <Paperclip size={20} />
            </button>
 
            {/* Text Input & Placeholder */}
            <div className="relative flex-1">
              <input
                type="text"
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                className={`flex-1 border-0 outline-0 rounded-md py-2 text-base bg-transparent w-full font-normal ${inputValue && !/^https?:\/\//.test(inputValue) ? 'text-red-500' : ''}`}
                style={{ position: "relative", zIndex: 1 }}
                onFocus={handleActivate}
              />
              <div className="absolute left-0 top-0 w-full h-full pointer-events-none flex items-center px-3 py-2">
                <AnimatePresence mode="wait">
                  {showPlaceholder && !isActive && !inputValue && (
                    <motion.span
                      key={placeholderIndex}
                      className="absolute left-0 top-1/2 -translate-y-1/2 text-gray-400 select-none pointer-events-none"
                      style={{
                        whiteSpace: "nowrap",
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                        zIndex: 0,
                      }}
                      variants={placeholderContainerVariants}
                      initial="initial"
                      animate="animate"
                      exit="exit"
                    >
                      {PLACEHOLDERS[placeholderIndex]
                        .split("")
                        .map((char, i) => (
                          <motion.span
                            key={i}
                            variants={letterVariants}
                            style={{ display: "inline-block" }}
                          >
                            {char === " " ? "\u00A0" : char}
                          </motion.span>
                        ))}
                    </motion.span>
                  )}
                </AnimatePresence>
              </div>
            </div>
 
            <button
              className="flex items-center gap-1 bg-black hover:bg-zinc-700 text-white p-3 rounded-full font-medium justify-center"
              title="Send"
              type="button"
              onClick={handleSubmit}
              disabled={loading}
            >
              <Send size={18} />
            </button>
          </div>
 
          {/* Expanded Controls */}
          <motion.div
            className="w-full flex justify-start px-4 items-center text-sm"
            variants={{
              hidden: {
                opacity: 0,
                y: 20,
                pointerEvents: "none" as const,
                transition: { duration: 0.25 },
              },
              visible: {
                opacity: 1,
                y: 0,
                pointerEvents: "auto" as const,
                transition: { duration: 0.35, delay: 0.08 },
              },
            }}
            initial="hidden"
            animate={isActive || inputValue ? "visible" : "hidden"}
            style={{ marginTop: 8 }}
          >
            <div className="flex gap-3 items-center">
              {/* Custom Domain Toggle */}
              <button
                className={`flex items-center gap-1 px-4 py-2 rounded-full transition-all font-medium group ${
                  thinkActive
                    ? "bg-blue-600/10 outline outline-blue-600/60 text-blue-950"
                    : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
                title="Custom Domain"
                type="button"
                onClick={(e) => {
                  e.stopPropagation();
                  setThinkActive((a) => !a);
                }}
              >
                <Globe
                  className="group-hover:fill-yellow-300 transition-all"
                  size={18}
                />
                Custom Domain
              </button>
 
              {/* Custom Alias Toggle */}
              <motion.button
                className={`flex items-center px-4 gap-1 py-2 rounded-full transition font-medium whitespace-nowrap overflow-hidden justify-start  ${
                  customAliasActive
                    ? "bg-blue-600/10 outline outline-blue-600/60 text-blue-950"
                    : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
                title="Custom Alias"
                type="button"
                onClick={(e) => {
                  e.stopPropagation();
                  setCustomAliasActive((a) => !a);
                }}
                initial={false}
                animate={{
                  width: customAliasActive ? 125 : 36,
                  paddingLeft: customAliasActive ? 8 : 9,
                }}
              >
                <div className="flex-1">
                  <Globe size={18} />
                </div>
                <motion.span
                className="pb-[2px]"
                  initial={false}
                  animate={{
                    opacity: customAliasActive ? 1 : 0,
                  }}
                >
                  Custom alias
                </motion.span>
              </motion.button>
            </div>
          </motion.div>
 
          {/* Result or Error */}
          {shortUrl && (
            <div className="mt-4 text-green-600">
              Short URL: <a href={shortUrl} target="_blank" rel="noopener noreferrer">{shortUrl}</a>
            </div>
          )}
          {error && <div className="mt-4 text-red-600">{error}</div>}
 
          {thinkActive && (
            <input
              type="text"
              placeholder="Enter custom domain"
              className="mt-2 px-3 py-1 border border-gray-300 rounded-md"
              value={customDomain}
              onChange={(e) => setCustomDomain(e.target.value)}
            />
          )}
 
          {customAliasActive && (
            <input
              type="text"
              placeholder="Enter custom alias"
              className="mt-2 px-3 py-1 border border-gray-300 rounded-md"
              value={customAlias}
              onChange={(e) => setCustomAlias(e.target.value)}
            />
          )}
        </div>
      </motion.div>
    </div>
  );
};
 
export { ShortLinkInput };